package com.example.server_foregin_languages.dto;

import com.example.server_foregin_languages.domain.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordSetResponse {
    private Long id;
    private String toLanguage;
    private String title;
    private String description;
    private List<Word> wordList;
    private Instant creationTime;
    private Boolean isShared;
}
