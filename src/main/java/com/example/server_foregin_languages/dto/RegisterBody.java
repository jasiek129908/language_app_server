package com.example.server_foregin_languages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBody {
    @NotBlank(message = "Email can not be blank")
    private String email;
    @NotBlank(message = "Nickname can not be blank")
    private String nickName;
    @NotBlank(message = "Password can not be blank")
    private String password;
}
