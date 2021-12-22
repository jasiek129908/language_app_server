package com.example.server_foregin_languages.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedWordSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer likeCount;
    private Integer dislikeCount;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "word_set_id")
    private WordSet wordSet;

    @JsonBackReference
    @OneToMany(mappedBy = "sharedWordSet")
    private List<Vote> likeList;

    @JsonManagedReference
    @OneToMany(mappedBy = "sharedWordSet")
    private List<Comment> commentList;
}
