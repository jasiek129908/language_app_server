package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.Word;
import com.example.server_foregin_languages.domain.WordSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word,Long> {

    Optional<Word> findByWord(String word);
    Optional<Word> findByTranslation(String translation);
    Integer countByWord(String word);
    Integer countByTranslation(String translation);

    @Transactional
    @Modifying
    @Query("DELETE FROM Word u WHERE u.wordSet = ?1")
    void deleteByWordSet(WordSet wordSet);

}
