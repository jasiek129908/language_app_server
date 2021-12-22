package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.Comment;
import com.example.server_foregin_languages.dto.CommentBody;
import com.example.server_foregin_languages.dto.CommentResponse;
import com.example.server_foregin_languages.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    private ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable("id") Long sharedWordSetId){
        List<CommentResponse> allComments = commentService.getAllComments(sharedWordSetId);
        return ResponseEntity.status(HttpStatus.OK).body(allComments);
    }

    @PostMapping
    private ResponseEntity<Comment> addComment(@RequestBody CommentBody commentBody){
        System.out.println(commentBody.toString());
        Comment comment = commentService.saveComment(commentBody);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}









