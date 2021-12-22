package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.ChangePasswordBody;
import com.example.server_foregin_languages.dto.ChangedEmailResponse;
import com.example.server_foregin_languages.dto.NicknameChangeResponse;
import com.example.server_foregin_languages.dto.ResponseMessage;
import com.example.server_foregin_languages.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/nick")
    public ResponseEntity<NicknameChangeResponse> changeNickName(@RequestParam("nick") String newNickname) {
        AppUser appUser = userService.changeUserNickName(newNickname);
        return ResponseEntity.ok(new NicknameChangeResponse(appUser.getNickName()));
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseMessage> changePassword(@RequestBody ChangePasswordBody changePassword) {
        ResponseMessage responseMessage = userService.changeUserPassword(changePassword);
        if (responseMessage.getMessage().equals("Old password was incorrect")) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(responseMessage);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        }
    }

    @PostMapping("/email")
    public ResponseEntity<ChangedEmailResponse> changeEmail(@RequestParam String email) {
        ChangedEmailResponse changedEmailResponse = userService.changeUserEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(changedEmailResponse);
    }

}
