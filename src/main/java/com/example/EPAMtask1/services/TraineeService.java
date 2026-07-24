package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.info("Creating trainee with firstName: {}, lastName: {}", firstName, lastName);
        Trainee trainee = new Trainee();
        int userId = traineeDao.generateNextId();

        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setPassword(credentialsGenerator.generatePassword());
        trainee.setUserId(userId);
        trainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName));

        traineeDao.createTrainee(trainee);
        logger.debug("Trainee created successfully with ID: {} and username: {}", trainee.getUserId(), trainee.getUsername());
        return trainee;
    }

    public void updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.info("Updating trainee with ID: {}", id);
        Trainee trainee = selectTrainee(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        traineeDao.updateTrainee(trainee);
        logger.debug("Trainee with ID: {} updated successfully", id);
    }

    public void deleteTrainee(int id) {
        logger.info("Deleting trainee with ID: {}", id);
        selectTrainee(id);
        traineeDao.deleteTrainee(id);
        logger.debug("Trainee with ID: {} deleted successfully", id);
    }

    public Trainee selectTrainee(int id) {
        logger.debug("Selecting trainee with ID: {}", id);
        return traineeDao.selectTrainee(id).orElseThrow(() -> {
            logger.error("Trainee with ID: {} not found", id);
            return new IllegalArgumentException("Trainee with ID " + id + " not found");
        });
    }
}
