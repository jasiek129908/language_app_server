package com.example.server_foregin_languages.scheduled;

import com.example.server_foregin_languages.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final UserActivityService userActivityService;

    //zamienic na crona zeby pierwszego dnia nowego mieisace
    @Scheduled(fixedRate = 86400000)
//    @Scheduled(fixedRate = 60000)
    public void removeOldActivity() {
        log.info("Task started {}", dateFormat.format(new Date()));
        userActivityService.removeActivityFromOneMonthAgo();

    }

}
