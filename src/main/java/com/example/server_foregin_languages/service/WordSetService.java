package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Word;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.WordSetBody;
import com.example.server_foregin_languages.dto.WordSetUpdateBody;
import com.example.server_foregin_languages.dto.WordUpdateBody;
import com.example.server_foregin_languages.mapper.UtilMapper;
import com.example.server_foregin_languages.repo.AppUserRepository;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import com.example.server_foregin_languages.repo.WordRepository;
import com.example.server_foregin_languages.repo.WordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WordSetService {

    private final WordSetRepository wordSetRepository;
    private final UtilMapper utilMapper;
    private final AppUserRepository appUserRepository;
    private final WordRepository wordRepository;
    private final SharedWordSetRepository sharedWordSetRepository;

    public WordSet saveWordSet(WordSetBody wordSetBody) {
        WordSet wordSet = utilMapper.mapFromDtoToWordSet(wordSetBody);
        wordSet.setCreationTime(Instant.now());
        return wordSetRepository.save(wordSet);
    }

    public List<WordSet> findAllUserWordSets(String email) {
        return wordSetRepository.getAllByUser(appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("email not found " + email)));
    }

    public String[] getAllAvailableLanguages() {
        return Locale.getISOCountries();
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

    public WordSet findWordSetById(Long id) {
        return wordSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No word set with id: " + id + " found"));
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
            if (!wordRepository.findByWord(word.getWord()).isPresent()
                    && !wordRepository.findByTranslation(word.getTranslation()).isPresent()) {
                Word word1 = new Word();
                word1.setWord(word.getWord());
                word1.setTranslation(word.getTranslation());
                word1.setDescription(word.getDescription());
                word1.setWordSet(wordSet);
                wordRepository.save(word1);
                newWords.add(word1);
                continue;
            }
        }
    }

    public SharedWordSet sharedWordSet(Long wordSetId) {
        Optional<WordSet> wordSet = wordSetRepository.findById(wordSetId);
        if (wordSet.isPresent()) {
            if (sharedWordSetRepository.getByWordSetId(wordSetId) == null) {
                SharedWordSet sharedWordSet = new SharedWordSet();
                sharedWordSet.setDislikeCount(0);
                sharedWordSet.setLikeCount(0);
                sharedWordSet.setWordSet(wordSet.get());
                sharedWordSetRepository.save(sharedWordSet);
                return sharedWordSet;
            }
        } else {
            throw new RuntimeException("wordSet with id: " + wordSetId + " not found");
        }
        return null;
    }

    public List<WordSet> getPageSharedWordSet(int pageNumber, int pageSize) {
        return wordSetRepository.findPageWordSet(PageRequest.of(pageNumber, pageSize));
    }

    public int getQuantityOfAvailablePages(Integer pageSize) {
        float res = wordSetRepository.count() * 1.0f / pageSize;
        return (int) (res % 1 == 0 ? res : res + 1);
    }
}
