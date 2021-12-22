package com.example.server_foregin_languages.dto;

import com.example.server_foregin_languages.domain.WordSet;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordBody {
    private String word;
    private String translation;
    private String description;
}
