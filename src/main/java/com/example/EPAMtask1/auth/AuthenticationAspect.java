package com.example.EPAMtask1.auth;

import com.example.EPAMtask1.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class AuthenticationAspect {
    private final AuthenticationService authenticationService;

    @Before("@annotation(Authentication) && args(username, password, ..)")
    public void checkAuth(String username, String password) {
        authenticationService.authenticate(username, password);
    }
}
