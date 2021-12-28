package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.SortingType;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.*;
import com.example.server_foregin_languages.service.WordSetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/wordset")
@AllArgsConstructor
public class WordSetController {

    private final WordSetService wordSetService;


    @GetMapping("/languages")
    public ResponseEntity<List<Country>> getAvailableLanguages() {
        return ResponseEntity.status(HttpStatus.OK).body(wordSetService.getAllAvailableLanguages());
    }

    @PostMapping
    public ResponseEntity<WordSet> createNewWordSet(@RequestBody WordSetBody wordSetBody) {
        System.out.println(wordSetBody);
        WordSet wordSet = wordSetService.saveWordSet(wordSetBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(wordSet);
    }

    @PutMapping
    public ResponseEntity<WordSet> updateWordSet(@RequestBody WordSetUpdateBody wordSetBody) {
        WordSet wordSet = wordSetService.updateWordSet(wordSetBody);
        return ResponseEntity.status(HttpStatus.OK).body(wordSet);
    }

    @GetMapping("/userall")
    public ResponseEntity<List<WordSetResponse>> getAllUserWordSet(@RequestParam String email) {
        List<WordSetResponse> allUserWordSets = wordSetService.findAllUserWordSets(email);
        return ResponseEntity.status(HttpStatus.OK).body(allUserWordSets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordSetResponse> getWordSetById(@PathVariable Long id) {
        WordSetResponse wordSet = wordSetService.findWordSetById(id);
        return ResponseEntity.status(HttpStatus.OK).body(wordSet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WordSet> deleteWordSet(@PathVariable Long id) {
        WordSet wordSet = wordSetService.deleteWordSetById(id);
        return ResponseEntity.status(HttpStatus.OK).body(wordSet);
    }

    @PostMapping("/share")
    public ResponseEntity<SharedWordSet> shareWordSet(@RequestBody Long wordSetId) {
        SharedWordSet sharedWordSet = wordSetService.sharedWordSet(wordSetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sharedWordSet);
    }

    @GetMapping("/page")
    public ResponseEntity<List<WordSetResponse>> getPageWordSet(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam SortingType sortingType,
                                                                @RequestParam String filter) {
        if (pageNumber - 1 < 0)
            throw new RuntimeException("pageNumber lower than zero");
        List<WordSetResponse> pageSharedWordSet = wordSetService.getPageWordSet(pageNumber - 1, pageSize, sortingType, filter);
        return ResponseEntity.ok(pageSharedWordSet);
    }

    @GetMapping("/allpagesize")
    public ResponseEntity<Integer> getAllPageSize(@RequestParam Integer pageSize, @RequestParam("text") String searchText, @RequestParam String filter) {
        int quantityOfAvailablePages = wordSetService.getQuantityOfAvailablePages(pageSize, searchText, filter);
        return ResponseEntity.ok(quantityOfAvailablePages);
    }

    @GetMapping("/search/{text}")
    public ResponseEntity<List<WordSetResponse>> getSearchWordSet(@PathVariable String text, @RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                          @RequestParam SortingType sortingType, @RequestParam String filter) {
        List<WordSetResponse> searchedWordSet = wordSetService.searchByText(text, pageNumber - 1, pageSize, sortingType, filter);
        return ResponseEntity.ok(searchedWordSet);
    }
}
