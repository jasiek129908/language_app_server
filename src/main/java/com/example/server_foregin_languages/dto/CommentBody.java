package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CommentBody {
    private String userEmail;
    private Long sharedWordSetId;
    private String comment;
}
