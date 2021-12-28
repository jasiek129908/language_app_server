package com.example.server_foregin_languages.sockets;

import com.example.server_foregin_languages.domain.GameType;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Word;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.sockets.game.Game;
import com.example.server_foregin_languages.sockets.game.dto.DuelInvitationBody;
import com.example.server_foregin_languages.sockets.game.dto.DuelNextWordGameType;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.*;

@Controller
@AllArgsConstructor
public class MessageController {

    private SimpUserRegistry simpUserRegistry;

    private final SimpMessageSendingOperations messagingTemplate;
    private final SharedWordSetRepository sharedWordSetRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private Map<String, String> nicknameToUID;
    private List<Game> gamesList;


    @MessageMapping("/register")
    public void assignUserNickToUID(String message, Principal user) {
        nicknameToUID.put(message, user.getName());
    }

    @MessageMapping("/toUser/invitation")
    public void sendDuelInvitationToUser(DuelInvitationBody duelBody, Principal principal) {
        String UID = nicknameToUID.get(duelBody.getNickname());
        if (UID == null) {
            throw new RuntimeException("can not find user UID with nickname: " + duelBody.getNickname());
        }
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(duelBody.getSharedWordSetId())
                .orElseThrow(() -> new RuntimeException("shared word set not found with id: " + duelBody.getSharedWordSetId()));
        gamesList.add(new Game(principal.getName(), nicknameToUID.get(duelBody.getNickname()), sharedWordSet));
        simpMessagingTemplate.convertAndSendToUser(UID, "/queue/private/acceptDuel", duelBody);
    }

    @MessageMapping("/invitation/accept")
    public void sendDuelInvitationToUser(boolean isAccepted, Principal opponent) {
        if (isAccepted) {
            Optional<Game> first = gamesList.stream().filter(game -> game.getOpponent().equals(opponent.getName())).findFirst();
            Game game = first.orElseThrow(() -> new RuntimeException("can not find game for user with UID " + opponent.getName() + " .Shouldn't ever trigger"));
            game.setAccepted(true);

            game.generateRandomGameTypeForTask();
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/gameStarted", new DuelNextWordGameType(
                    game.getSharedWordSet().getId()
                    , getNextWord(game, 0)
                    , getGameType(game, 0)
            ));
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/gameStarted", new DuelNextWordGameType(
                    game.getSharedWordSet().getId()
                    , getNextWord(game, 0)
                    , getGameType(game, 0)
            ));
        } else {
            Optional<Game> first = gamesList.stream().filter(game -> game.getOpponent().equals(opponent.getName())).findFirst();
            Game game = first.orElseThrow(() -> new RuntimeException("can not find game for user with UID " + opponent.getName() + " .Shouldn't ever trigger"));
            gamesList.remove(game);
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/rejected", "null");
        }
    }

    @MessageMapping("/invitation/remove")
    public void removeGame(Principal principal) {
        Optional<Game> first = gamesList.stream().filter(game -> game.getGameOwner().equals(principal.getName())).findFirst();
        Game game = first.orElseThrow(() -> new RuntimeException("can not find game for user with UID " + principal.getName() + " .Shouldn't ever trigger"));
        String opponent = game.getOpponent();
        gamesList.remove(game);
        simpMessagingTemplate.convertAndSendToUser(opponent, "/queue/private/remove", "null");
    }

    @MessageMapping("/game/progress")
    public void sendPlayerProgress(Principal principal) {
        Optional<Game> first = gamesList.stream()
                .filter(game -> game.getGameOwner().equals(principal.getName()) || game.getOpponent().equals(principal.getName()))
                .findFirst();
        Game game = first.orElseThrow(() -> new RuntimeException("can not find game for user with UID " + principal.getName() + " .Shouldn't ever trigger"));
        game.generateRandomGameTypeForTask();
        if (game.getGameOwner().equals(principal.getName())) {
            game.getOwnerProgress().add(true);
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/gameProgress", "null");
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/nextWord", new DuelNextWordGameType(
                    game.getSharedWordSet().getId()
                    , getNextWord(game, game.getOwnerProgress().size())
                    , getGameType(game, game.getOwnerProgress().size())
            ));

        } else {
            game.getOpponentProgress().add(true);
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/gameProgress", "null");
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/nextWord", new DuelNextWordGameType(
                    game.getSharedWordSet().getId()
                    , getNextWord(game, game.getOpponentProgress().size())
                    , getGameType(game, game.getOpponentProgress().size())
            ));

        }
    }

    @MessageMapping("/game/winner")
    public void sendWinner(Principal principal) {
        Optional<Game> first = gamesList.stream()
                .filter(game -> game.getGameOwner().equals(principal.getName()) || game.getOpponent().equals(principal.getName()))
                .findFirst();
        Game game = first.orElseThrow(() -> new RuntimeException("can not find game for user with UID " + principal.getName() + " .Shouldn't ever trigger"));
        if (game.getGameOwner().equals(principal.getName())) {
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/winner", true);
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/winner", false);
        } else {
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/winner", true);
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/winner", false);
        }
        gamesList.remove(game);
    }



    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        System.out.println(exception);
        messagingTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

    private GameType getGameType(Game game, int index) {
        int i = 0;
        for (Map.Entry<String, GameType> entry : game.getWordToType().entrySet()) {
            GameType value = entry.getValue();
            if (i == index) {
                return value;
            }
            i++;
        }
        return null;
    }

    private String getNextWord(Game game, int index) {
        int i = 0;
        for (Map.Entry<String, GameType> entry : game.getWordToType().entrySet()) {
            String key = entry.getKey();
            if (i == index) {
                return key;
            }
            i++;
        }
        return null;
    }
}
