package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.*;
import com.example.server_foregin_languages.dto.*;
import com.example.server_foregin_languages.mapper.UtilMapper;
import com.example.server_foregin_languages.repo.AppUserRepository;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.repo.WordRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WordSetService {

    private final WordSetRepository wordSetRepository;
    private final UtilMapper utilMapper;
    private final AppUserRepository appUserRepository;
    private final AuthService authService;
    private final WordRepository wordRepository;
    private final SharedWordSetRepository sharedWordSetRepository;

    public WordSet saveWordSet(WordSetBody wordSetBody) {
        WordSet wordSet = utilMapper.mapFromDtoToWordSet(wordSetBody);
        wordSet.setCreationTime(Instant.now());
        return wordSetRepository.save(wordSet);
    }

    public List<WordSetResponse> findAllUserWordSets(String email) {
        return wordSetRepository.getAllByUser(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("email not found " + email)))
                .stream()
                .map(wordSet -> utilMapper.mapFromWordSetToDto(wordSet))
                .collect(Collectors.toList());
    }

    public List<Country> getAllAvailableLanguages() {
        Set<Country> collect = Arrays.stream(Locale.getISOLanguages())
                .map(Locale::new)
                .map(locale -> new Country(locale.toString(), locale.getDisplayName(Locale.getDefault())))
                .collect(Collectors.toSet());
        List<Country> response = new ArrayList<>(collect);
        response.sort(Comparator.comparing(Country::getName));
        return response;
    }

    public WordSet deleteWordSetById(Long id) {
        Optional<WordSet> wordSet = wordSetRepository.findById(id);
        if (wordSet.isPresent()) {
            wordSetRepository.deleteById(id);
        } else {
            throw new RuntimeException("No word set with id: " + id + " found");
        }
        return wordSet.get();
    }

    public WordSetResponse findWordSetById(Long id) {
        WordSet wordSet = wordSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No word set with id: " + id + " found"));
        return utilMapper.mapFromWordSetToDto(wordSet);
    }

    public WordSet updateWordSet(WordSetUpdateBody updatedWordSet) {
        Optional<WordSet> wordSetOptional = wordSetRepository.findById(updatedWordSet.getId());
        WordSet wordSet = wordSetOptional.orElseThrow(() -> new RuntimeException("No word set with id: " + updatedWordSet.getId() + " found"));
        wordSet.setTitle(updatedWordSet.getTitle());
        wordSet.setDescription(updatedWordSet.getDescription());
        updatedWordList(updatedWordSet.getWordList(), wordSet);
        wordSetRepository.save(wordSet);
        return wordSet;
    }

    private void updatedWordList(List<WordUpdateBody> newList, WordSet wordSet) {
        wordRepository.deleteByWordSet(wordSet);
        List<Word> newWords = new ArrayList<>();
        for (WordUpdateBody word : newList) {
            Word word1 = new Word();
            word1.setWord(word.getWord());
            word1.setTranslation(word.getTranslation());
            word1.setDescription(word.getDescription());
            word1.setWordSet(wordSet);
            wordRepository.save(word1);
            newWords.add(word1);
        }
    }

    public SharedWordSet sharedWordSet(Long wordSetId) {
        Optional<WordSet> wordSet = wordSetRepository.findById(wordSetId);
        if (wordSet.isPresent()) {
            if (!sharedWordSetRepository.getByWordSet(wordSet.get()).isPresent()) {
                SharedWordSet sharedWordSet = new SharedWordSet();
                sharedWordSet.setLikesCount(0);
                sharedWordSet.setWordSet(wordSet.get());
                sharedWordSetRepository.save(sharedWordSet);
                return sharedWordSet;
            }
        } else {
            throw new RuntimeException("wordSet with id: " + wordSetId + " not found");
        }
        return null;
    }

    public List<WordSetResponse> getPageWordSet(int pageNumber, int pageSize, SortingType sortingType, String languagesToFilter) {
        AppUser loggedInUser = authService.getLoggedInUser();
        Pageable paging = makePageRequest(pageNumber, pageSize, sortingType);
        List<String> langFilter = splitFilterToList(languagesToFilter);

        List<WordSetResponse> collect;
        if (langFilter.size() > 0) {
            collect = wordSetRepository.findPageWordSetWithFilter(loggedInUser, langFilter, paging).stream()
                    .map(wordSet -> utilMapper.mapFromWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        } else {
            collect = wordSetRepository.findPageWordSetWithoutFilter(loggedInUser, paging).stream()
                    .map(wordSet -> utilMapper.mapFromWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    public int getQuantityOfAvailablePages(Integer pageSize, String searchText, String languagesToFilter) {
        AppUser loggedInUser = authService.getLoggedInUser();
        List<String> langFilter = splitFilterToList(languagesToFilter);
        float res = 0;
        if (searchText == null) {
            res = wordSetRepository.countByUser(loggedInUser) * 1.0f / pageSize;
        } else {
            if (langFilter.size() > 0) {
                res = wordSetRepository.getAmountOfPagesForSearchingWithFilter(searchText, loggedInUser, langFilter) * 1.0f / pageSize;
            } else {
                res = wordSetRepository.getAmountOfPagesForSearchingWithoutFilter(searchText, loggedInUser) * 1.0f / pageSize;
            }

        }
        return (int) (res % 1 == 0 ? res : res + 1);
    }

    public List<WordSetResponse> searchByText(String text, int pageNumber, int pageSize, SortingType sortingType, String languagesToFilter) {
        Pageable paging = makePageRequest(pageNumber, pageSize, sortingType);
        List<String> langFilter = splitFilterToList(languagesToFilter);
        AppUser loggedInUser = authService.getLoggedInUser();

        List<WordSetResponse> collect;
        if (langFilter.size() > 0) {
            collect = wordSetRepository.searchInWordSetPageWithFilter(text, loggedInUser, langFilter, paging).stream()
                    .map(wordSet -> utilMapper.mapFromWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        } else {
            collect = wordSetRepository.searchInWordSetPageWithoutFilter(text, loggedInUser, paging).stream()
                    .map(wordSet -> utilMapper.mapFromWordSetToDto(wordSet))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    private Pageable makePageRequest(int pageNumber, int pageSize, SortingType sortingType) {
        Pageable paging = null;
        if (sortingType == SortingType.DATE_ASC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").ascending());
        } else if (sortingType == SortingType.DATE_DESC) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());
        }
        return paging;
    }

    private List<String> splitFilterToList(String languagesToFilter) {
        String[] temp = languagesToFilter.split(",");
        return new ArrayList<>(Arrays.asList(temp))
                .stream()
                .filter(lang -> lang.length() > 0)
                .map(lang -> StringUtils.trimAllWhitespace(lang))
                .collect(Collectors.toList());
    }
}

