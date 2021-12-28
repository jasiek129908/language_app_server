package com.example.server_foregin_languages.mapper;

import com.example.server_foregin_languages.domain.*;
import com.example.server_foregin_languages.dto.*;
import com.example.server_foregin_languages.repo.AppUserRepository;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UtilMapper {

    private final AppUserRepository appUserRepository;
    private final WordSetRepository wordSetRepository;
    private final SharedWordSetRepository sharedWordSetRepository;

    public AppUser mapFromDtoToUser(RegisterBody registerBody) {
        AppUser appUser = new AppUser();
        appUser.setNickName(registerBody.getNickName());
        appUser.setEmail(registerBody.getEmail());
        appUser.setPassword(registerBody.getPassword());
        return appUser;
    }

    public WordSet mapFromDtoToWordSet(WordSetBody wordSetBody) {
        WordSet wordSet = new WordSet();
        wordSet.setTitle(wordSetBody.getTitle());
        wordSet.setToLanguage(wordSetBody.getToLanguage());
        wordSet.setDescription(wordSetBody.getDescription());
        wordSet.setUser(appUserRepository.findByEmail(wordSetBody.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")));
        List<Word> wordList = mapFromDtoToWordList(wordSetBody.getWordList(), wordSet);
        wordSet.setWordList(wordList);
        return wordSet;
    }

    private List<Word> mapFromDtoToWordList(List<WordBody> wordList, WordSet wordSet) {
        List<Word> list = new ArrayList<>();
        wordList.stream()
                .forEach((word) -> {
                    Word temp = new Word();
                    temp.setWord(word.getWord());
                    temp.setDescription(word.getDescription());
                    temp.setTranslation(word.getTranslation());
                    temp.setWordSet(wordSet);
                    list.add(temp);
                });
        return list;
    }

    public Statistic mapFromDtoToStatistic(StatisticBody statisticBody) {
        Statistic statistic = new Statistic();
        statistic.setCreateDate(Instant.now());
        statistic.setGameType(statisticBody.getGameType());
        statistic.setTimeOfGameplay(statisticBody.getTimeOfGameplay());
        statistic.setUser(appUserRepository.findByEmail(statisticBody.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")));
        statistic.setWordSet(wordSetRepository.findById(statisticBody.getWordSetId())
                .orElseThrow(() -> new RuntimeException("WordSet not found")));
        statistic.setMistakesList(mapFromDtoToMistakesPerWord(statisticBody.getMistakesList(), statistic));
        return statistic;
    }

    private List<MistakesPerWord> mapFromDtoToMistakesPerWord(List<MistakesPerWordBody> mistakesList, Statistic statistic) {
        List<MistakesPerWord> list = new ArrayList<>();
        mistakesList.stream()
                .forEach((stat) -> {
                    MistakesPerWord temp = new MistakesPerWord();
                    temp.setWord(stat.getWord());
                    temp.setErrorCounter(stat.getErrorCounter());
                    temp.setStatistic(statistic);
                    list.add(temp);
                });
        return list;
    }

    public WordSetResponse mapFromWordSetToDto(WordSet wordSet) {
        return WordSetResponse.builder()
                .id(wordSet.getId())
                .toLanguage(wordSet.getToLanguage())
                .title(wordSet.getTitle())
                .description(wordSet.getDescription())
                .wordList(wordSet.getWordList())
                .creationTime(wordSet.getCreationTime())
                .isShared(sharedWordSetRepository.existsByWordSet(wordSet))
                .build();
    }

    public SharedWordSetResponse mapFromSharedWordSetToDto(SharedWordSet sharedWordSet){
        return SharedWordSetResponse.builder()
                .id(sharedWordSet.getId())
                .wordSet(sharedWordSet.getWordSet())
                .likesCount(sharedWordSet.getLikesCount())
                .author(sharedWordSet.getWordSet().getUser().getNickName())
                .build();
    }
}
