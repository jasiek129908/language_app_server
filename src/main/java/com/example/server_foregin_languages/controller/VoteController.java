package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.dto.UserVotesToPostResponse;
import com.example.server_foregin_languages.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    @GetMapping
    public List<UserVotesToPostResponse> getUserSharedWordSetVotes(){
        return voteService.getAllPostVoteForUser();
    }

}
