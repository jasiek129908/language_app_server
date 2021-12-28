package com.example.server_foregin_languages.aspect;

import com.example.server_foregin_languages.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityAspect {

    private final UserActivityService userActivityService;
    @Pointcut("execution(* com.example.server_foregin_languages.controller.StatisticController.saveStatistic(..))")
    public void savingStatisticPointcut() {
    }
    @After("savingStatisticPointcut()")
    public void saveUserActivity() {
        userActivityService.saveUserActivity();
    }
}