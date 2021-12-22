package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordUpdateBody {
    private Long id;
    private String word;
    private String translation;
    private String description;
}
