package com.example.server_foregin_languages.sockets.game.dto;

import com.example.server_foregin_languages.domain.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuelNextWordGameType {
    private Long sharedWordSetId;
    private GameType gameType;
}
