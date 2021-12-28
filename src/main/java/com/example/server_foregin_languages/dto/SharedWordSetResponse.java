package com.example.server_foregin_languages.dto;

import com.example.server_foregin_languages.domain.WordSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedWordSetResponse {

    private Long id;
    private Integer likesCount;
    private WordSet wordSet;
    private String author;
}
