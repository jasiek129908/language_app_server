package com.example.server_foregin_languages.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private GameType gameType;
    private Instant createDate;
    //in seconds
    private Integer timeOfGameplay;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "word_set_id")
    private WordSet wordSet;
    @OneToMany(mappedBy = "statistic", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<MistakesPerWord> mistakesList;

}
