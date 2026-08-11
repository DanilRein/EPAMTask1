package com.example.EPAMtask1.util;

import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UserCredentialsGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsGenerator.class);

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public String generatePassword() {
        logger.debug("Generating new password");
        String password = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        logger.debug("Password generated successfully");
        return password;
    }

    public String generateUsername(String firstName, String lastName) {
        logger.debug("Generating username for firstName: {}, lastName: {}", firstName, lastName);
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase();
        long suffix = countMatchingNames(firstName, lastName);
        if(suffix > 0) {
            username += suffix;
        }
        logger.debug("Username generated successfully: {}", username);
        return username;
    }

    public long countMatchingNames(String firstName, String lastName) {
        logger.debug("Counting matching names for firstName: {}, lastName: {}", firstName, lastName);
        long trainerCount = trainerRepository.countByUser_FirstNameAndUser_LastName(firstName, lastName);
        long traineeCount = traineeRepository.countByUser_FirstNameAndUser_LastName(firstName, lastName);
        logger.debug("Count of matching names: {}", traineeCount + trainerCount);
        return trainerCount+traineeCount;
    }

}
