package com.example.server_foregin_languages.sockets.game;

import com.example.server_foregin_languages.domain.GameType;
import com.example.server_foregin_languages.domain.SharedWordSet;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
public class Game {
    private String gameOwner;
    private String opponent;
    private Boolean accepted;
    private Integer gameProgress;
    private SharedWordSet sharedWordSet;
    //maps each word to random game type
    private Map<Integer, GameType> wordToType;

    public Game(String gameOwner, String opponent, SharedWordSet sharedWordSet) {
        this.gameOwner = gameOwner;
        this.opponent = opponent;
        this.sharedWordSet = sharedWordSet;
        this.accepted = false;
        this.gameProgress = 0;
        this.wordToType = new HashMap<>();
    }

    public void generateRandomGameTypeForTask() {
        int rand = new Random().nextInt(3);
        switch (rand) {
            case 0:
                wordToType.put(gameProgress, GameType.SCATTER);
                System.out.println("bedzie scatter");
                break;
            case 1:
                if (sharedWordSet.getWordSet().getWordList().get(gameProgress).getDescription() != null
                        && sharedWordSet.getWordSet().getWordList().get(gameProgress).getDescription().length() > 0) {
                    wordToType.put(gameProgress, GameType.DESCRIPTION);
                    System.out.println("bedzie description");
                } else {
                    System.out.println("nie bedzie description");
                    wordToType.put(gameProgress, GameType.SCATTER);
                }
                break;
            case 2:
                wordToType.put(gameProgress, GameType.TRANSLATION);
                System.out.println("bedzie transaltion");
                break;
        }
    }


}
