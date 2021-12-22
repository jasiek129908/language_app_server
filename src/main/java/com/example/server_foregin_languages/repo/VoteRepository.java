package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.SharedWordSet;
import com.example.server_foregin_languages.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findBySharedWordSetAndUser(SharedWordSet sharedWordSet, AppUser appUser);
    Optional<Vote> findBySharedWordSetAndUserAndIsUp(SharedWordSet sharedWordSet, AppUser appUser,Boolean isUp);
}
