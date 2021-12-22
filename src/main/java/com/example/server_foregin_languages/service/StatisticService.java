package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.GameType;
import com.example.server_foregin_languages.domain.Statistic;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.StatisticBody;
import com.example.server_foregin_languages.mapper.UtilMapper;
import com.example.server_foregin_languages.repo.StatisticRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticService {

    private final StatisticRepository statisticRepository;
    private final UtilMapper utilMapper;
    private final WordSetRepository wordSetRepository;
    private final AuthService authService;

    public Statistic saveStatistic(StatisticBody statisticBody) {
        return statisticRepository.save(utilMapper.mapFromDtoToStatistic(statisticBody));
    }

    public List<WordSet> getAllWordSetWithStatisticByGameType(GameType gameType) {
        AppUser loggedInUser = authService.getLoggedInUser();
        List<Long> wordSetIdList = statisticRepository.findAll().stream()
                .filter(statistic -> statistic.getGameType().equals(gameType))
                .filter(statistic -> statistic.getUser().equals(loggedInUser))
                .map(statistic -> statistic.getWordSet().getId())
                .distinct()
                .collect(Collectors.toList());

        List<WordSet> res = new ArrayList<>();
        for (Long wordSetId : wordSetIdList) {
            res.add(wordSetRepository.getById(wordSetId));
        }
        return res;
    }

    public List<Statistic> getStatisticForWordSetId(Long wordSetId) {
        AppUser loggedInUser = authService.getLoggedInUser();
        WordSet wordSet = wordSetRepository.getById(wordSetId);
        List<Statistic> collect = statisticRepository.findAllByWordSet(wordSet).stream()
                .filter(statistic -> statistic.getUser().equals(loggedInUser))
                .sorted(new Comparator<Statistic>() {
                    @Override
                    public int compare(Statistic o1, Statistic o2) {
                        return o1.getCreateDate().compareTo(o2.getCreateDate());
                    }
                })
                .collect(Collectors.toList());
        return collect;
    }
}
