package com.example.server_foregin_languages.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id")
    private AppUser author;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="shared_word_set_id")
    private SharedWordSet sharedWordSet;

    private String comment;
    private Instant creationDate;

}
