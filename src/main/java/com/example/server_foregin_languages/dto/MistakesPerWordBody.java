package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MistakesPerWordBody {
    private String word;
    private Integer errorCounter;
}
