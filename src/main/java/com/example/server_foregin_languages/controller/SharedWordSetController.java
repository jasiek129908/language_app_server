package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.service.SharedWordSetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sharedwordset")
@AllArgsConstructor
public class SharedWordSetController {

    private final SharedWordSetService sharedWordSetService;

    @GetMapping("/{id}")
    public ResponseEntity<SharedWordSet> getSharedWordSet(@PathVariable("id") Long wordSetId) {
        SharedWordSet sharedWordSet = sharedWordSetService.getSharedWordSetById(wordSetId);
        return ResponseEntity.status(HttpStatus.OK).body(sharedWordSet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSharedWordSet(@PathVariable("id") Long wordSetId) {
        sharedWordSetService.deleteWordSet(wordSetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SharedWordSet>> getAllSharedWordSet() {
        List<SharedWordSet> list = sharedWordSetService.getAllSharedWordSet();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/like")
    public ResponseEntity<SharedWordSet> likeSharedWordSet(@RequestBody Long id) {
        SharedWordSet sharedWordSet = sharedWordSetService.likeSharedWordSet(id);
        return ResponseEntity.status(HttpStatus.OK).body(sharedWordSet);
    }

    @PostMapping("/dislike")
    public ResponseEntity<SharedWordSet> dislikeSharedWordSet(@RequestBody Long id) {
        SharedWordSet sharedWordSet = sharedWordSetService.dislikeWordSet(id);
        return ResponseEntity.status(HttpStatus.OK).body(sharedWordSet);
    }

    @GetMapping("/page")
    public ResponseEntity<List<SharedWordSet>> getPageWordSet(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        if (pageNumber - 1 < 0)
            throw new RuntimeException("pageNumber lower than zero");
        List<SharedWordSet> pageSharedWordSet = sharedWordSetService.getPageSharedWordSet(pageNumber - 1, pageSize);
        return ResponseEntity.ok(pageSharedWordSet);
    }

    @GetMapping("/allpagesize")
    public ResponseEntity<Integer> getAllPageSize(@RequestParam Integer pageSize) {
        int quantityOfAvailablePages = sharedWordSetService.getQuantityOfAvailablePages(pageSize);
        return ResponseEntity.ok(quantityOfAvailablePages);
    }
}
