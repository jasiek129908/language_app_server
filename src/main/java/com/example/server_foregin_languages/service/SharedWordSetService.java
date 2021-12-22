package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Vote;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.repo.VoteRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SharedWordSetService {

    private final SharedWordSetRepository sharedWordSetRepository;
    private final WordSetRepository wordSetRepository;
    private final AuthService authService;
    private final VoteRepository voteRepository;

    public SharedWordSet getSharedWordSetById(Long id) {
        return sharedWordSetRepository.findById(id).orElseThrow(() -> new RuntimeException("no shared word set with id: " + id + " found"));
    }

    public void deleteWordSet(Long wordSetId) {
        sharedWordSetRepository.deleteByWordSet(wordSetRepository.getById(wordSetId));
    }

    public List<SharedWordSet> getAllSharedWordSet() {
        return sharedWordSetRepository.findAll();
    }

    public SharedWordSet likeSharedWordSet(Long sharedWordSetId) {
        AppUser loggedInUser = authService.getLoggedInUser();
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(sharedWordSetId)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + sharedWordSetId + " found"));

        if (!voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).isPresent()) {
            Vote vote = new Vote();
            vote.setSharedWordSet(sharedWordSet);
            vote.setIsUp(true);
            vote.setUser(loggedInUser);
            voteRepository.save(vote);

            sharedWordSet.setLikeCount(sharedWordSet.getLikeCount() + 1);
            sharedWordSetRepository.save(sharedWordSet);
        } else if (voteRepository.findBySharedWordSetAndUserAndIsUp(sharedWordSet, loggedInUser, true).isPresent()) {
            //unlike user clicked second time on thumbs_up
            Vote vote = voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).get();
            voteRepository.deleteById(vote.getId());
            sharedWordSet.setLikeCount(sharedWordSet.getLikeCount() - 1);
            sharedWordSetRepository.save(sharedWordSet);
        }
        return sharedWordSet;
    }

    public SharedWordSet dislikeWordSet(Long sharedWordSetId) {
        AppUser loggedInUser = authService.getLoggedInUser();
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(sharedWordSetId)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + sharedWordSetId + " found"));

        if (!voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).isPresent()) {
            Vote vote = new Vote();
            vote.setSharedWordSet(sharedWordSet);
            vote.setIsUp(false);
            vote.setUser(loggedInUser);
            voteRepository.save(vote);

            sharedWordSet.setDislikeCount(sharedWordSet.getDislikeCount() - 1);
            sharedWordSetRepository.save(sharedWordSet);
        } else if (voteRepository.findBySharedWordSetAndUserAndIsUp(sharedWordSet, loggedInUser, false).isPresent()) {
            //unlike user clicked second time on thumbs_up
            Vote vote = voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).get();
            voteRepository.deleteById(vote.getId());
            sharedWordSet.setDislikeCount(sharedWordSet.getDislikeCount() + 1);
            sharedWordSetRepository.save(sharedWordSet);
        }
        return sharedWordSet;
    }

    public List<SharedWordSet> getPageSharedWordSet(int pageNumber, int pageSize) {
        return sharedWordSetRepository.findPageSharedWordSet(PageRequest.of(pageNumber, pageSize));
    }

    public int getQuantityOfAvailablePages(Integer pageSize) {
        float res = sharedWordSetRepository.count() * 1.0f / pageSize;
        return (int) (res % 1 == 0 ? res : res + 1);
    }
}
