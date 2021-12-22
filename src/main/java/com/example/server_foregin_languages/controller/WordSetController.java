package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.WordSet;
import com.example.server_foregin_languages.dto.WordSetBody;
import com.example.server_foregin_languages.dto.WordSetUpdateBody;
import com.example.server_foregin_languages.service.WordSetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.List;

@RestController
@RequestMapping("/api/wordset")
@AllArgsConstructor
public class WordSetController {

    private final WordSetService wordSetService;


    @GetMapping("/languages")
    public ResponseEntity<String[]> getAvailableLanguages() {
        return ResponseEntity.status(HttpStatus.OK).body(wordSetService.getAllAvailableLanguages());
    }

    @PostMapping
    public ResponseEntity<WordSet> createNewWordSet(@RequestBody WordSetBody wordSetBody) {
        WordSet wordSet = wordSetService.saveWordSet(wordSetBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(wordSet);
    }

    @PutMapping
    public ResponseEntity<WordSet> updateWordSet(@RequestBody WordSetUpdateBody wordSetBody) {
        WordSet wordSet = wordSetService.updateWordSet(wordSetBody);
        return ResponseEntity.status(HttpStatus.OK).body(wordSet);
    }

    @GetMapping("/userall")
    public ResponseEntity<List<WordSet>> getAllUserWordSet(@RequestParam String email) {
        List<WordSet> allUserWordSets = wordSetService.findAllUserWordSets(email);
        return ResponseEntity.status(HttpStatus.OK).body(allUserWordSets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordSet> getWordSetById(@PathVariable Long id) {
        WordSet wordSet = wordSetService.findWordSetById(id);
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
    public ResponseEntity<List<WordSet>> getPageWordSet(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        if(pageNumber-1<0)
            throw new RuntimeException("pageNumber lower than zero");
        List<WordSet> pageSharedWordSet = wordSetService.getPageSharedWordSet(pageNumber-1, pageSize);
        return ResponseEntity.ok(pageSharedWordSet);
    }

    @GetMapping("/allpagesize")
    public ResponseEntity<Integer> getAllPageSize(@RequestParam Integer pageSize){
        int quantityOfAvailablePages = wordSetService.getQuantityOfAvailablePages(pageSize);
        return ResponseEntity.ok(quantityOfAvailablePages);
    }
}
