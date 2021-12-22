package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WordSetBody {
//    private String fromLanguage;
    private Long id;
    private String toLanguage;
    private String title;
    private String email;
    private String description;
    private List<WordBody> wordList;
}
