package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken can not be blank")
    private String refreshToken;
    @NotBlank(message = "email can not be blank")
    private String email;
}
