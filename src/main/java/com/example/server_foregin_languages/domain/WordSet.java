package com.example.server_foregin_languages.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private String toLanguage;
    private String title;
    @Column(length = 400)
    private String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    @JsonManagedReference
    @OneToMany(mappedBy = "wordSet", cascade = {CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.EAGER)
    private List<Word> wordList;
    private Instant creationTime;

    @EqualsAndHashCode.Exclude
    @JsonBackReference
    @OneToOne(mappedBy = "wordSet",cascade = {CascadeType.REMOVE} )
    private SharedWordSet sharedWordSet;
}
