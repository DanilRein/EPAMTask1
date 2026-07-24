package com.example.EPAMtask1.util;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserCredentialsGenerator {
    
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;

    public String generatePassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int suffix = 1;
        while (isUsernameTaken(username)) {
            username = baseUsername + suffix++;
        }
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
