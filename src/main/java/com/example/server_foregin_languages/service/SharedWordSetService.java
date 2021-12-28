package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Vote;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.domain.SortingType;
import com.example.server_foregin_languages.dto.SharedWordSetResponse;
import com.example.server_foregin_languages.mapper.UtilMapper;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.repo.VoteRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SharedWordSetService {

    private final SharedWordSetRepository sharedWordSetRepository;
    private final WordSetRepository wordSetRepository;
    private final AuthService authService;
    private final VoteRepository voteRepository;
    private final UtilMapper utilMapper;

    public SharedWordSetResponse getSharedWordSetByWordSetId(Long id) {
        WordSet wordSet = wordSetRepository.findById(id).
                orElseThrow(() -> new RuntimeException("No word set with id: " + id + " found"));

        SharedWordSet sharedWordSet = sharedWordSetRepository.getByWordSet(wordSet)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + id + " found"));
        return utilMapper.mapFromSharedWordSetToDto(sharedWordSet);
    }

    @Transactional
    public void deleteWordSet(Long wordSetId) {
        WordSet wordSet = wordSetRepository.findById(wordSetId).
                orElseThrow(() -> new RuntimeException("No word set with id: " + wordSetId + " found"));
        sharedWordSetRepository.deleteSharedWordSetByWordSet(wordSet);
    }

    public List<SharedWordSetResponse> getAllSharedWordSet() {
        return sharedWordSetRepository.findAll().stream()
                .map(wordSet -> utilMapper.mapFromSharedWordSetToDto(wordSet))
                .collect(Collectors.toList());
    }

    public SharedWordSet likeSharedWordSet(Long sharedWordSetId) {
        AppUser loggedInUser = authService.getLoggedInUser();
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(sharedWordSetId)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + sharedWordSetId + " found"));

        if (voteRepository.findBySharedWordSetAndUserAndIsUp(sharedWordSet, loggedInUser, false).isPresent()) {
            Vote vote = voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).get();
            voteRepository.deleteById(vote.getId());

            Vote newVote = new Vote();
            newVote.setSharedWordSet(sharedWordSet);
            newVote.setIsUp(true);
            newVote.setUser(loggedInUser);
            voteRepository.save(newVote);

            sharedWordSet.setLikesCount(sharedWordSet.getLikesCount() + 2);
            sharedWordSetRepository.save(sharedWordSet);
        } else if (!voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).isPresent()) {
            Vote vote = new Vote();
            vote.setSharedWordSet(sharedWordSet);
            vote.setIsUp(true);
            vote.setUser(loggedInUser);
            voteRepository.save(vote);

            sharedWordSet.setLikesCount(sharedWordSet.getLikesCount() + 1);
            sharedWordSetRepository.save(sharedWordSet);
        }
        return sharedWordSet;
    }

    public SharedWordSet dislikeWordSet(Long sharedWordSetId) {
        AppUser loggedInUser = authService.getLoggedInUser();
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(sharedWordSetId)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + sharedWordSetId + " found"));

        if (voteRepository.findBySharedWordSetAndUserAndIsUp(sharedWordSet, loggedInUser, true).isPresent()) {
            Vote vote = voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).get();
            voteRepository.deleteById(vote.getId());

            Vote newVote = new Vote();
            newVote.setSharedWordSet(sharedWordSet);
            newVote.setIsUp(false);
            newVote.setUser(loggedInUser);
            voteRepository.save(newVote);

            sharedWordSet.setLikesCount(sharedWordSet.getLikesCount() - 2);
            sharedWordSetRepository.save(sharedWordSet);
        } else if (!voteRepository.findBySharedWordSetAndUser(sharedWordSet, loggedInUser).isPresent()) {
            Vote vote = new Vote();
            vote.setSharedWordSet(sharedWordSet);
            vote.setIsUp(false);
            vote.setUser(loggedInUser);
            voteRepository.save(vote);

            sharedWordSet.setLikesCount(sharedWordSet.getLikesCount() - 1);
            sharedWordSetRepository.save(sharedWordSet);
        }
        return sharedWordSet;
    }

    public int getQuantityOfAvailablePages(Integer pageSize, String searchText, String languagesToFilter) {
        List<String> langFilter = splitFilterToList(languagesToFilter);
        float res;
        if (searchText == null) {
            res = sharedWordSetRepository.count() * 1.0f / pageSize;
        } else {
            if (langFilter.size() > 0) {
                res = sharedWordSetRepository.getAmountOfPagesForSearchingWithFilter(searchText, langFilter) * 1.0f / pageSize;
            } else {
                res = sharedWordSetRepository.getAmountOfPagesForSearchingWithoutFilter(searchText) * 1.0f / pageSize;
            }
        }
        return (int) (res % 1 == 0 ? res : res + 1);
    }

    public List<SharedWordSetResponse> getPageSharedWordSet(int pageNumber, int pageSize, SortingType sortingType, String languagesToFilter) {
        Pageable paging = makePageRequest(pageNumber, pageSize, sortingType);
        List<String> langFilter = splitFilterToList(languagesToFilter);

        List<SharedWordSetResponse> collect;
        if (langFilter.size() > 0) {
            collect = sharedWordSetRepository.findPageSharedWordSetWithFilter(langFilter, paging).stream()
                    .map(wordSet -> utilMapper.mapFromSharedWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        } else {
            collect = sharedWordSetRepository.findPageSharedWordSetWithoutFilter(paging).stream()
                    .map(wordSet -> utilMapper.mapFromSharedWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    public List<SharedWordSetResponse> searchByText(String text, int pageNumber, int pageSize, SortingType sortingType, String languagesToFilter) {
        Pageable paging = makePageRequest(pageNumber, pageSize, sortingType);
        List<String> langFilter = splitFilterToList(languagesToFilter);

        List<SharedWordSetResponse> collect;
        if (langFilter.size() > 0) {
            collect = sharedWordSetRepository.searchInSharedWordSetPageWithFilter(text, langFilter, paging).stream()
                    .map(wordSet -> utilMapper.mapFromSharedWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        } else {
            collect = sharedWordSetRepository.searchInSharedWordSetPageWithoutFilter(text, paging).stream()
                    .map(wordSet -> utilMapper.mapFromSharedWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    private List<String> splitFilterToList(String languagesToFilter) {
        String[] temp = languagesToFilter.split(",");
        return new ArrayList<>(Arrays.asList(temp))
                .stream()
                .filter(lang -> lang.length() > 0)
                .map(lang-> StringUtils.trimAllWhitespace(lang))
                .collect(Collectors.toList());
    }

    private Pageable makePageRequest(int pageNumber, int pageSize, SortingType sortingType) {
        Pageable paging = null;
        if (sortingType == SortingType.DATE_ASC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("wordSet.creationTime").ascending());
        } else if (sortingType == SortingType.DATE_DESC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("wordSet.creationTime").descending());
        } else if (sortingType == SortingType.LIKE_ASC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("likesCount").ascending());
        } else if (sortingType == SortingType.LIKE_DESC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("likesCount").descending());
        }
        return paging;
    }
}
