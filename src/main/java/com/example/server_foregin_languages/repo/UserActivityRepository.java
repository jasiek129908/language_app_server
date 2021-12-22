package com.example.server_foregin_languages.repo;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Optional<UserActivity> findByDateAndUser(Date date,AppUser user);
    List<UserActivity> findAllByUser(AppUser user);
}
