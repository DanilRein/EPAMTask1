package com.example.EPAMtask1.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();
    public void loginFailed(String username){
        if(attempts.containsKey(username))
            attempts.replace(username,attempts.get(username)+1);
        else attempts.put(username,1);
        if(attempts.get(username)==3)
            blockedUntil.put(username, LocalDateTime.now().plusMinutes(5));
    }
    public boolean isBlocked(String username){
        LocalDateTime until = blockedUntil.get(username);
        if(until==null)
            return false;
        if(LocalDateTime.now().isAfter(until)){
            blockedUntil.remove(username);
            attempts.remove(username);
            return false;
        }
        return true;
    }
    public void loginSucceed(String username){
        attempts.remove(username);
        blockedUntil.remove(username);
    }
}
