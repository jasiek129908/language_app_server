package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.Comment;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.dto.CommentBody;
import com.example.server_foregin_languages.dto.CommentResponse;
import com.example.server_foregin_languages.repo.AppUserRepository;
import com.example.server_foregin_languages.repo.CommentRepository;
import com.example.server_foregin_languages.repo.SharedWordSetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SharedWordSetRepository sharedWordSetRepository;
    private final AppUserRepository appUserRepository;

    public List<CommentResponse> getAllComments(Long sharedWordSetId) {
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(sharedWordSetId)
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + sharedWordSetId + " found while fetching comments"));
        List<Comment> commentList = commentRepository.getBySharedWordSet(sharedWordSet);
        System.out.println("rozmiar "+commentList.size());
        List<CommentResponse> result = commentList.stream().map(comment -> CommentResponse.builder()
                .comment(comment.getComment())
                .authorNickName(comment.getAuthor().getNickName())
                .creationDate(comment.getCreationDate())
                .id(comment.getId())
                .build())
                .collect(Collectors.toList());
        return result;
    }

    public Comment saveComment(CommentBody commentBody) {
        AppUser appUser = appUserRepository.findByEmail(commentBody.getUserEmail())
                .orElseThrow(() -> new RuntimeException("no user with id: " + commentBody.getSharedWordSetId() + " found while adding comment"));
        SharedWordSet sharedWordSet = sharedWordSetRepository.findById(commentBody.getSharedWordSetId())
                .orElseThrow(() -> new RuntimeException("no shared word set with id: " + commentBody.getSharedWordSetId() + " found while adding comment"));
        Comment comment = new Comment();
        comment.setComment(commentBody.getComment());
        comment.setAuthor(appUser);
        comment.setSharedWordSet(sharedWordSet);
        comment.setCreationDate(Instant.now());
        return commentRepository.save(comment);
    }
}
