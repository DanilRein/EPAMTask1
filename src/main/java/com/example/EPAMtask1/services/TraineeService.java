package com.example.EPAMtask1.services;

import com.example.EPAMtask1.auth.SecurityUtils;
import com.example.EPAMtask1.exception.AuthenticationException;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import io.micrometer.core.annotation.Counted;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final PasswordEncoder passwordEncoder;
    private final TraineeRepository traineeRepository;
    private final UserCredentialsGenerator credentialsGenerator;
    private final TrainerRepository trainerRepository;

    @Counted(value = "trainee.created", description = "Number of trainees created")
    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        logger.info("Creating trainee with firstName: {}, lastName: {}", firstName, lastName);
        Trainee trainee = new Trainee();
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        user.setPassword(passwordEncoder.encode(credentialsGenerator.generatePassword()));
        user.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainee.setUser(user);
        traineeRepository.save(trainee);
        logger.debug("Trainee created successfully with ID: {} and username: {}", trainee.getId(), trainee.getUser().getUsername());
        return trainee;
    }

    public void updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
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

    // Overloaded method to update trainee by username and also update the active status
    public void updateTrainee(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
        logger.info("Updating trainee with username: {}", username);
        Trainee trainee = findTraineeByUsername(username);
        User user = trainee.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        user.setActive(isActive);
        traineeRepository.save(trainee);
        logger.debug("Trainee with username: {} updated successfully", username);
    }


    @Transactional
    public void deleteTrainee() {
        String username = SecurityUtils.getCurrentUsername();
        Trainee trainee = findTraineeByUsername(username);
        logger.info("Deleting trainee with ID: {}", trainee.getId());
        List<Trainer> trainers = trainee.getTrainers() != null
                ? new ArrayList<>(trainee.getTrainers())
                : new ArrayList<>();
        for (Trainer trainer : trainers) {
            trainer.getTrainees().remove(trainee);
            trainerRepository.save(trainer);
        }
        traineeRepository.delete(trainee);
        logger.debug("Trainee with ID: {} deleted successfully", trainee.getId());
    }

    public Trainee selectTrainee(String username) {
        logger.debug("Selecting trainee with username: {}", username);
        return findTraineeByUsername(username);
    }

    @Transactional
    public List<Trainer> updateTraineeTrainers(int traineeId, List<Integer> trainerIds) {
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

    @Transactional
    public List<Trainer> updateTraineeTrainersByUsername(String traineeUsername, List<String> trainerUsernames) {
        logger.info("Updating trainers for trainee with username: {}", traineeUsername);
        Trainee trainee = findTraineeByUsername(traineeUsername);
        List<Trainer> oldTrainers = new ArrayList<>(trainee.getTrainers());
        for(Trainer oldTrainer : oldTrainers) {
            oldTrainer.getTrainees().remove(trainee);
            trainerRepository.save(oldTrainer);
        }
        List<Trainer> newTrainers = new ArrayList<>();
        for(String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElseThrow(() -> {
                logger.warn("Trainer with username: {} not found", trainerUsername);
                return new IllegalArgumentException("Trainer with username " + trainerUsername + " not found");
            });
            trainer.getTrainees().add(trainee);
            trainerRepository.save(trainer);
            newTrainers.add(trainer);
        }
        logger.debug("Trainers updated successfully for trainee with username: {}", traineeUsername);
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
        logger.info("Changing password for user: {}", authUsername);
        Trainee trainee = findTraineeByUsername(authUsername);
        if(!passwordEncoder.matches(oldPassword, trainee.getUser().getPassword())) {
            logger.warn("Authentication failed: username or password is invalid");
            throw new AuthenticationException("Username or password is invalid");
        }
        trainee.getUser().setPassword(passwordEncoder.encode(newPassword));
        traineeRepository.save(trainee);
        logger.debug("Password changed successfully for user: {}", authUsername);
    }
    public void setActiveStatus(String username, boolean isActive) {
        Trainee trainee = findTraineeByUsername(username);
        trainee.getUser().setActive(isActive);
        traineeRepository.save(trainee);
        logger.info("Setting active status for trainee with ID: {} to {}", trainee.getId(), isActive);
    }

    public void toggleActiveStatus(int id) {
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
