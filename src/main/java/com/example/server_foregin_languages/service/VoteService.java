package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.Vote;
import com.example.server_foregin_languages.dto.UserVotesToPostResponse;
import com.example.server_foregin_languages.repo.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final AuthService authService;

    public List<UserVotesToPostResponse> getAllPostVoteForUser() {
        AppUser loggedInUser = authService.getLoggedInUser();
        List<Vote> allByUser = voteRepository.findAllByUser(loggedInUser);
        return allByUser.stream()
                .map(vote -> new UserVotesToPostResponse(vote.getSharedWordSet().getId(), vote.getIsUp()))
                .collect(Collectors.toList());
    }

}
