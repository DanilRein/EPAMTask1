package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private UserCredentialsGenerator credentialsGenerator;
    private AuthenticationService authenticationService;
    private TrainerRepository trainerRepository;


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

    @Transactional
    public void deleteTrainee(String authUsername, String authPassword) {
        authenticationService.authenticate(authUsername, authPassword);
        Trainee trainee = findTraineeByUsername(authUsername);
        logger.info("Deleting trainee with ID: {}", trainee.getId());
        List<Trainer> trainers = new ArrayList<>(trainee.getTrainers());
        for (Trainer trainer : trainers) {
            trainer.getTrainees().remove(trainee);
            trainerRepository.save(trainer);
        }
        traineeRepository.delete(trainee);
        logger.debug("Trainee with ID: {} deleted successfully", trainee.getId());
    }

    public Trainee selectTrainee(String authUsername, String authPassword, String username) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.debug("Selecting trainee with username: {}", username);
        return findTraineeByUsername(username);
    }

    @Transactional
    public List<Trainer> updateTraineeTrainers(String authUsername, String authPassword, int traineeId, List<Integer> trainerIds) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Updating trainers for trainee with ID: {}", traineeId);
        Trainee trainee = findTraineeById(traineeId);
        List<Trainer> oldTrainers = new ArrayList<>(trainee.getTrainers());
        for(Trainer oldTrainer : oldTrainers) {
            oldTrainer.getTrainees().remove(trainee);
            trainerRepository.save(oldTrainer);
        }
        List<Trainer> newTrainers = new ArrayList<>();
        for(Integer trainerId : trainerIds) {
            Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> {
                logger.warn("Trainer with ID: {} not found", trainerId);
                return new IllegalArgumentException("Trainer with ID " + trainerId + " not found");
            });
            trainer.getTrainees().add(trainee);
            trainerRepository.save(trainer);
            newTrainers.add(trainer);
        }
        logger.debug("Trainers updated successfully for trainee with ID: {}", traineeId);
        return newTrainers;
    }

    public Trainee findTraineeById(int id) {
        return traineeRepository.findById(id).orElseThrow(() -> {
            logger.warn("Trainee with ID: {} not found", id);
            return new IllegalArgumentException("Trainee with ID " + id + " not found");
        });
    }

    public Trainee findTraineeByUsername(String username) {
        return traineeRepository.findByUser_Username(username).orElseThrow(() -> {
            logger.warn("Trainee with username: {} not found", username);
            return new IllegalArgumentException("Trainee with username " + username + " not found");
        });
    }

    public void changePassword(String authUsername, String oldPassword, String newPassword) {
        authenticationService.authenticate(authUsername, oldPassword);
        logger.info("Changing password for user: {}", authUsername);
        Trainee trainee = findTraineeByUsername(authUsername);
        trainee.getUser().setPassword(newPassword);
        traineeRepository.save(trainee);
        logger.debug("Password changed successfully for user: {}", authUsername);
    }

    public void toggleActiveStatus(String authUsername, String authPassword, int id) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Toggling active status for trainee with ID: {}", id);
        Trainee trainee = findTraineeById(id);
        if(trainee.getUser().isActive()) {
            trainee.getUser().setActive(false);
            logger.debug("Trainee with ID: {} deactivated", id);
        } else {
            trainee.getUser().setActive(true);
            logger.debug("Trainee with ID: {} activated", id);
        }
        traineeRepository.save(trainee);
    }
}
