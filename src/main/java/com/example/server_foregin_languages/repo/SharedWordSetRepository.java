package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.WordSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharedWordSetRepository extends JpaRepository<SharedWordSet, Long> {

    @Query("select p from SharedWordSet p inner join p.wordSet w WHERE " +
            "w.toLanguage in :languagesToFilter")
    List<SharedWordSet> findPageSharedWordSetWithFilter(List<String> languagesToFilter, Pageable pageable);
    @Query("select p from SharedWordSet p inner join p.wordSet w")
    List<SharedWordSet> findPageSharedWordSetWithoutFilter(Pageable pageable);

    Optional<SharedWordSet> getByWordSet(WordSet wordSet);
    Boolean existsByWordSet(WordSet wordSet);
    Long deleteSharedWordSetByWordSet(WordSet wordSet);

    @Query("select p from SharedWordSet p inner join p.wordSet w WHERE " +
            "w.toLanguage in :languagesToFilter and "+
            "(upper(w.title) like upper(concat('%', :text,'%')) or upper(w.description) like upper(concat('%', :text,'%')) or " +
            "upper(w.user.nickName) like upper(concat('%', :text,'%')))")
    List<SharedWordSet> searchInSharedWordSetPageWithFilter(String text, List<String> languagesToFilter, Pageable pageable);

    @Query("select p from SharedWordSet p inner join p.wordSet w WHERE " +
            "upper(w.title) like upper(concat('%', :text,'%')) or upper(w.description) like upper(concat('%', :text,'%')) or " +
            "upper(w.user.nickName) like upper(concat('%', :text,'%'))")
    List<SharedWordSet> searchInSharedWordSetPageWithoutFilter(String text, Pageable pageable);


    @Query("select COUNT(w) from SharedWordSet p inner join p.wordSet w WHERE " +
            "w.toLanguage in :languagesToFilter and "+
            "(upper(w.title) like upper(concat('%', :text,'%')) or upper(w.description) like upper(concat('%',:text,'%')) or " +
            "upper(w.user.nickName) like upper(concat('%', :text,'%')))")
    Integer getAmountOfPagesForSearchingWithFilter(String text, List<String> languagesToFilter);

    @Query("select COUNT(w) from SharedWordSet p inner join p.wordSet w WHERE " +
            "upper(w.title) like upper(concat('%', :text,'%')) or upper(w.description) like upper(concat('%',:text,'%')) or " +
            "upper(w.user.nickName) like upper(concat('%', :text,'%'))")
    Integer getAmountOfPagesForSearchingWithoutFilter(String text);

}
