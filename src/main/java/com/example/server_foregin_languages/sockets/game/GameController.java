package com.example.server_foregin_languages.sockets.game;

import com.example.server_foregin_languages.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@AllArgsConstructor
public class GameController {

    private final UserService userService;

    @GetMapping("{nickname}")
    public boolean checkIfNicknameExists(@PathVariable String nickname) {
        return userService.checkIfAnyUserHasNickname(nickname);
    }

}
