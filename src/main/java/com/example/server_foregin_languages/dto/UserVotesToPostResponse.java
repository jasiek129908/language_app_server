package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVotesToPostResponse {
    private Long sharedWordSetId;
    private Boolean isUp;
}
