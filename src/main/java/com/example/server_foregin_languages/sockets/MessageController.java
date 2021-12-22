package com.example.server_foregin_languages.sockets;

import com.example.server_foregin_languages.domain.SharedWordSet;
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
    //contains user UIDs
    private List<Game> gamesList;

    @MessageMapping("/duel")
    @SendTo({"/topic/reply", "/topic/xd"})
    public String processMessageFromClient(String message, SimpMessageHeaderAccessor ha, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
        String name = new Gson().fromJson(message, Map.class).get("name").toString();
        simpUserRegistry.getUsers().forEach(System.out::println);
        return name;
    }

    @MessageMapping("/register")
    @SendTo({"/topic/reply", "/queue/xd"})
    public void assignUserNickToUID(String message, Principal user) throws Exception {
        nicknameToUID.put(message, user.getName());
        System.out.println("\n\n");
        for (Map.Entry<String, String> xd : nicknameToUID.entrySet()) {
            System.out.println(xd);
        }
        System.out.println("\n\n");
    }

    @MessageMapping("/toUser")
    public void assignUserNickToUID(String message) throws Exception {
        String toUser = new Gson().fromJson(message, Map.class).get("toUser").toString();
        String text = new Gson().fromJson(message, Map.class).get("text").toString();
        String UID = nicknameToUID.get(toUser);
        if (UID == null) {
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(UID, "/queue/private", text);
    }

    @MessageMapping("/toUser/invitation")
    public void sendDuelInvitationToUser(DuelInvitationBody duelBody, Principal principal) throws Exception {
        String UID = nicknameToUID.get(duelBody.getNickname());
        if (UID == null) {
            throw new RuntimeException("can not find user UID with nickname" + duelBody.getNickname());
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
//            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/gameStarted", game.getSharedWordSet().getId());
//            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/gameStarted", game.getSharedWordSet().getId());
            simpMessagingTemplate.convertAndSendToUser(game.getOpponent(), "/queue/private/gameStarted", new DuelNextWordGameType(
                    game.getSharedWordSet().getId(), game.getWordToType().get(0)
            ));
            simpMessagingTemplate.convertAndSendToUser(game.getGameOwner(), "/queue/private/gameStarted", new DuelNextWordGameType(
                    game.getSharedWordSet().getId(), game.getWordToType().get(0)
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
        simpMessagingTemplate.convertAndSendToUser(opponent, "/queue/private/remove", null);
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        System.out.println("\n\n\nException w sokeetach: " + exception.getMessage());
        messagingTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

}
