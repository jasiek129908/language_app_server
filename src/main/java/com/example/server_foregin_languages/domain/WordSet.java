package com.example.server_foregin_languages.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    private String fromLanguage;
    private String toLanguage;
    private String title;
    private String description;
    //@JsonIgnore chwiliowo rozwiaze problem
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    @JsonManagedReference
    @OneToMany(mappedBy = "wordSet", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Word> wordList;
    private Instant creationTime;

    @JsonBackReference
    @OneToOne(mappedBy = "wordSet",cascade = {CascadeType.PERSIST,CascadeType.REMOVE} )
    private SharedWordSet sharedWordSet;
}
