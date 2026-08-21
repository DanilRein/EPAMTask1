package com.example.EPAMtask1.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null)
            throw new IllegalArgumentException("No authenticated user found in security context");
        return authentication.getName();
    }
}
