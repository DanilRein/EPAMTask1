package com.example.EPAMtask1.services;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    Set<String> jwtBlackList = ConcurrentHashMap.newKeySet();
    public void blacklist(String token){
        jwtBlackList.add(token);
        logger.debug("JWT token blacklisted");
    }
    public boolean isBlackListed(String token){
        return jwtBlackList.contains(token);
    }
}
