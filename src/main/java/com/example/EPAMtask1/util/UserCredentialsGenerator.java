package com.example.EPAMtask1.util;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserCredentialsGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsGenerator.class);
    
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;

    public String generatePassword() {
        logger.debug("Generating new password");
        String password = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        logger.debug("Password generated successfully");
        return password;
    }

    public String generateUsername(String firstName, String lastName) {
        logger.debug("Generating username for firstName: {}, lastName: {}", firstName, lastName);
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int suffix = 1;
        while (isUsernameTaken(username)) {
            username = baseUsername + suffix++;
        }
        logger.debug("Username generated successfully: {}", username);
        return username;
    }

    private boolean isUsernameTaken(String username) {
        boolean inTrainees = traineeDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        boolean inTrainers = trainerDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        return inTrainees || inTrainers;
    }
}
