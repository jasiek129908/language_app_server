package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.WordSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordSetRepository extends JpaRepository<WordSet,Long> {
    List<WordSet> getAllByUser(AppUser user);

    @Query("select p from WordSet p")
    List<WordSet> findPageWordSet(Pageable pageable);
}
