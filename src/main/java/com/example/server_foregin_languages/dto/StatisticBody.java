package com.example.server_foregin_languages.dto;

import com.example.server_foregin_languages.domain.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class StatisticBody {

    private GameType gameType;
    private Integer timeOfGameplay;
    private String email;
    private Long wordSetId;
    private List<MistakesPerWordBody> mistakesList;
}
