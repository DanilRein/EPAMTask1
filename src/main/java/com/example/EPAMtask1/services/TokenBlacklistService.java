package com.example.EPAMtask1.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    Set<String> jwtBlackList = ConcurrentHashMap.newKeySet();
    public void blacklist(String token){
        jwtBlackList.add(token);
    }
    public boolean isBlackListed(String token){
        return jwtBlackList.contains(token);
    }
}
