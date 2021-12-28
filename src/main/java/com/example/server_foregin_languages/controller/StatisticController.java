package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.GameType;
import com.example.server_foregin_languages.domain.Statistic;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.StatisticBody;
import com.example.server_foregin_languages.service.StatisticService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@AllArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping
    public ResponseEntity<Statistic> saveStatistic(@RequestBody StatisticBody statisticBody) {
        Statistic statistic = statisticService.saveStatistic(statisticBody);
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }

    @GetMapping("/{type}")
    public List<WordSet> getAllWordSetWithStatistic(@PathVariable("type") GameType gameType) {
        return statisticService.getAllWordSetWithStatisticByGameType(gameType);
    }

    @GetMapping("/byid/{id}/{type}")
    public ResponseEntity<List<Statistic>> getAllStatisticByWordSetId(@PathVariable("id") Long wordSetId,@PathVariable GameType type){
        System.out.println(type+" "+wordSetId);
        List<Statistic> statistic = statisticService.getStatisticForWordSetIdAndType(wordSetId,type);
        System.out.println(statistic.size());
        return ResponseEntity.status(HttpStatus.OK).body(statistic);
    }
}
