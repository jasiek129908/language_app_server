package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.UserActivity;
import com.example.server_foregin_languages.repo.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityRepository activityRepository;
    private final AuthService authService;

    public void saveUserActivity() {
        if (!checkIfUserTodayHadActivity()) {
            UserActivity userActivity = new UserActivity();
            userActivity.setUser(authService.getLoggedInUser());
            userActivity.setDate(getNewDateWithoutTimeStamp(new Date()));
            activityRepository.save(userActivity);
        }
    }

    private Date getNewDateWithoutTimeStamp(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private boolean checkIfUserTodayHadActivity() {
        Optional<UserActivity> byDate = activityRepository.findByDateAndUser(getNewDateWithoutTimeStamp(new Date()),
                authService.getLoggedInUser());
        System.out.println("3");
        return byDate.isPresent();
    }

    @Transactional
    public void removeActivityFromOneMonthAgo() {
        List<UserActivity> entityToDelete = findEntitiesToDelete();
        for (UserActivity activity : entityToDelete) {
            activityRepository.deleteById(activity.getId());
        }
    }

    private List<UserActivity> findEntitiesToDelete() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(date);

        List<UserActivity> all = activityRepository.findAll();
        return all.stream()
                .filter(activity -> {
                    Period diff = Period.between(
                            LocalDate.parse(formatDate).withDayOfMonth(1),
                            LocalDate.parse(dateFormat.format(activity.getDate())).withDayOfMonth(1));
                    return diff.getMonths() != 0 ? true : false;
                })
                .collect(Collectors.toList());
    }
}
