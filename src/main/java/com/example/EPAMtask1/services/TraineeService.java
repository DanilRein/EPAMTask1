package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
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
    private TraineeRepository traineeRepository;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;
    @Autowired
    private AuthenticationService authenticationService;

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.info("Creating trainee with firstName: {}, lastName: {}", firstName, lastName);
        Trainee trainee = new Trainee();
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        user.setPassword(credentialsGenerator.generatePassword());
        user.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainee.setUser(user);
        traineeRepository.save(trainee);
        logger.debug("Trainee created successfully with ID: {} and username: {}", trainee.getId(), trainee.getUser().getUsername());
        return trainee;
    }

    public void updateTrainee(String authUsername, String authPassword, int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Updating trainee with ID: {}", id);
        Trainee trainee = findTraineeById(id);
        User user = trainee.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        traineeRepository.save(trainee);
        logger.debug("Trainee with ID: {} updated successfully", id);
    }

    public void deleteTrainee(String authUsername, String authPassword, int id) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Deleting trainee with ID: {}", id);
        findTraineeById(id);
        traineeRepository.deleteById(id);
        logger.debug("Trainee with ID: {} deleted successfully", id);
    }

    public Trainee selectTrainee(String authUsername, String authPassword, int id) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.debug("Selecting trainee with ID: {}", id);
        return findTraineeById(id);
    }

    private Trainee findTraineeById(int id) {
        return traineeRepository.findById(id).orElseThrow(() -> {
            logger.warn("Trainee with ID: {} not found", id);
            return new IllegalArgumentException("Trainee with ID " + id + " not found");
        });
    }
}
