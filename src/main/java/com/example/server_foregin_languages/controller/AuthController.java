package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.dto.*;
import com.example.server_foregin_languages.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody RegisterBody registerBody) {
        try {
            authService.registerUser(registerBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("could not create user"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("registered user"));
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody LoginBody loginBody) {
        return authService.loginUser(loginBody);
    }

    @PostMapping("/refreshToken")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthenticationResponse authenticationResponse = authService.refreshToken(refreshTokenRequest);
        return authenticationResponse;
    }
}
