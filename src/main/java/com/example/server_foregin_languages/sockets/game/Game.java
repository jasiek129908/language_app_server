package com.example.server_foregin_languages.sockets.game;

import com.example.server_foregin_languages.domain.GameType;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Word;
import lombok.Data;

import java.util.*;

@Data
public class Game {
    private String gameOwner;
    private String opponent;
    private Boolean accepted;
    private Integer gameProgress;
    private SharedWordSet sharedWordSet;
    //maps each word to random game type
    private Map<String, GameType> wordToType;
    private List<Boolean> ownerProgress;
    private List<Boolean> opponentProgress;

    public Game(String gameOwner, String opponent, SharedWordSet sharedWordSet) {
        this.gameOwner = gameOwner;
        this.opponent = opponent;
        this.sharedWordSet = sharedWordSet;
        this.accepted = false;
        this.gameProgress = 0;
        this.wordToType = new LinkedHashMap<>();
        this.ownerProgress = new ArrayList<>();
        this.opponentProgress = new ArrayList<>();
    }

    public void generateRandomGameTypeForTask() {
        if (wordToType.size() >= sharedWordSet.getWordSet().getWordList().size())
            return;
        int rand = new Random().nextInt(3);
        String word = getNextRandomWord();
        switch (rand) {
            case 0:
                wordToType.put(word, GameType.SCATTER);
                break;
            case 1:
                if (sharedWordSet.getWordSet().getWordList().get(gameProgress).getDescription() != null
                        && sharedWordSet.getWordSet().getWordList().get(gameProgress).getDescription().length() > 0) {
                    wordToType.put(word, GameType.DESCRIPTION);
                } else {
                    wordToType.put(word, GameType.SCATTER);
                }
                break;
            case 2:
                wordToType.put(word, GameType.TRANSLATION);
                break;
        }
        gameProgress++;
    }

    private String getNextRandomWord() {
        Set<String> words = wordToType.keySet();
        List<Word> wordList = sharedWordSet.getWordSet().getWordList();
        while (true) {
            int randIndex = new Random().nextInt(wordList.size());
            if (!words.contains(wordList.get(randIndex).getWord())) {
                return wordList.get(randIndex).getWord();
            }
        }
    }

}
