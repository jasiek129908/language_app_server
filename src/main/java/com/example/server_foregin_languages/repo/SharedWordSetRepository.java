package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.WordSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SharedWordSetRepository extends JpaRepository<SharedWordSet, Long> {

    @Query("select p from SharedWordSet p")
    List<SharedWordSet> findPageSharedWordSet(Pageable pageable);

    SharedWordSet getByWordSetId(Long wordSetId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SharedWordSet u WHERE u.wordSet = ?1")
    void deleteByWordSet(WordSet wordSet);
}
