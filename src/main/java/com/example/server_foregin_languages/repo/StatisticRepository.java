package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.Statistic;
import com.example.server_foregin_languages.domain.WordSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic,Long> {

    List<Statistic> findAllByWordSet(WordSet wordSet);
}
