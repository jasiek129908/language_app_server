package com.example.server_foregin_languages.controller;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.domain.UserActivity;
import com.example.server_foregin_languages.dto.ActivityResponse;
import com.example.server_foregin_languages.repo.UserActivityRepository;
import com.example.server_foregin_languages.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
@AllArgsConstructor
public class ActivityController {

    private final UserActivityRepository userActivityRepository;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getThisMonthActivity(){
        AppUser loggedInUser = authService.getLoggedInUser();
        List<UserActivity> allByUser = userActivityRepository.findAllByUser(loggedInUser);
        List<ActivityResponse> res = new ArrayList<>();
        for(UserActivity activity: allByUser){
            ActivityResponse activityResponse = new ActivityResponse();
            activityResponse.setDate(activity.getDate());
            res.add(activityResponse);
        }
        return ResponseEntity.ok(res);
    }

}
