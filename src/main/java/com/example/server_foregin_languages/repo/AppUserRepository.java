package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByNickName(String nickname);
}
