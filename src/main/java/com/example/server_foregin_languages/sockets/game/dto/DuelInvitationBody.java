package com.example.server_foregin_languages.sockets.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuelInvitationBody {

    private String nickname;
    private Long sharedWordSetId;
}
