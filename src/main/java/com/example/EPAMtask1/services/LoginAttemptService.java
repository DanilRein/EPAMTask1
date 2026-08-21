package com.example.EPAMtask1.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoginAttemptService {
    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();
    public void loginFailed(String username){
        logger.warn("Login failed for username: {}", username);
        if(attempts.containsKey(username))
            attempts.replace(username,attempts.get(username)+1);
        else attempts.put(username,1);
        if(attempts.get(username)==3) {
            blockedUntil.put(username, LocalDateTime.now().plusMinutes(5));
            logger.warn("Username blocked due to failed login attempts: {}", username);
        }
    }
    public boolean isBlocked(String username){
        LocalDateTime until = blockedUntil.get(username);
        if(until==null)
            return false;
        if(LocalDateTime.now().isAfter(until)){
            blockedUntil.remove(username);
            attempts.remove(username);
            logger.debug("Login block expired for username: {}", username);
            return false;
        }
        return true;
    }
    public void loginSucceed(String username){
        attempts.remove(username);
        blockedUntil.remove(username);
        logger.debug("Login succeeded for username: {}", username);
    }
}
