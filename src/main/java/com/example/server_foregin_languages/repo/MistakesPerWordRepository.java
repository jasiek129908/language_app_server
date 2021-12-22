package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.MistakesPerWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MistakesPerWordRepository extends JpaRepository<MistakesPerWord,Long> {
}
