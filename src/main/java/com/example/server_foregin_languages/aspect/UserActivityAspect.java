package com.example.server_foregin_languages.aspect;

import com.example.server_foregin_languages.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityAspect {

    private final UserActivityService userActivityService;

    //zamien na wywolanie gierek albo na save statiscitc
    @After("execution(* com.example.server_foregin_languages.controller..*(..))")
    public void doSomethingAdvice()  {
        userActivityService.saveUserActivity();
    }

}