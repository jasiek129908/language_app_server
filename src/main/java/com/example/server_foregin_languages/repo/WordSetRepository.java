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
public interface WordSetRepository extends JpaRepository<WordSet, Long> {
    List<WordSet> getAllByUser(AppUser user);

    Integer countByUser(AppUser user);

    @Query("select COUNT(p) from WordSet p  WHERE " +
            "p.user = :appUser and " +
            "p.toLanguage in :languagesToFilter and " +
            "(upper(p.title) like upper(concat('%', :text,'%')) or upper(p.description) like upper(concat('%',:text,'%')))")
    Integer getAmountOfPagesForSearchingWithFilter(String text, AppUser appUser, List<String> languagesToFilter);

    @Query("select COUNT(p) from WordSet p  WHERE " +
            "p.user = :appUser and " +
            "(upper(p.title) like upper(concat('%', :text,'%')) or upper(p.description) like upper(concat('%',:text,'%')))")
    Integer getAmountOfPagesForSearchingWithoutFilter(String text, AppUser appUser);

    @Query("select p from WordSet p where p.user = :appUser and " +
            "p.toLanguage in :languagesToFilter")
    List<WordSet> findPageWordSetWithFilter(AppUser appUser, List<String> languagesToFilter, Pageable pageable);

    @Query("select p from WordSet p where p.user = :appUser")
    List<WordSet> findPageWordSetWithoutFilter(AppUser appUser, Pageable pageable);

    @Query("select p from WordSet p WHERE " +
            "p.user = :appUser and " +
            "p.toLanguage in :languagesToFilter and " +
            "(upper(p.title) like upper(concat('%', :text,'%')) or upper(p.description) like upper(concat('%', :text,'%')))")
    List<WordSet> searchInWordSetPageWithFilter(String text,AppUser appUser, List<String> languagesToFilter, Pageable pageable);

    @Query("select p from WordSet p WHERE " +
            "p.user = :appUser and " +
            "(upper(p.title) like upper(concat('%', :text,'%')) or upper(p.description) like upper(concat('%', :text,'%')))")
    List<WordSet> searchInWordSetPageWithoutFilter(String text, AppUser appUser,Pageable pageable);

}
