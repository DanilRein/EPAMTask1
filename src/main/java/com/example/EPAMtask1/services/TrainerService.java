package com.example.EPAMtask1.services;

import com.example.EPAMtask1.auth.Authentication;
import com.example.EPAMtask1.exception.AuthenticationException;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import io.micrometer.core.annotation.Counted;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserCredentialsGenerator credentialsGenerator;
    private final PasswordEncoder passwordEncoder;
    @Counted(value = "trainer.created", description = "Number of trainers created")
    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        logger.info("Creating trainer with firstName: {}, lastName: {}, specialization: {}", firstName, lastName, specialization);
        User user = new User();
        Trainer trainer = new Trainer();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainer.setSpecialization(specialization);
        user.setPassword(passwordEncoder.encode(credentialsGenerator.generatePassword()));
        user.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainer.setUser(user);
        trainerRepository.save(trainer);
        logger.debug("Trainer created successfully with ID: {} and username: {}", trainer.getId(), trainer.getUser().getUsername());
        return trainer;
    }

    @Authentication
    public void updateTrainer(String authUsername, String authPassword, int id, String firstName, String lastName, TrainingType specialization) {
        logger.info("Updating trainer with ID: {}", id);
        Trainer trainer = findTrainerById(id);
        trainer.getUser().setFirstName(firstName);
        trainer.getUser().setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainerRepository.save(trainer);
        logger.debug("Trainer with ID: {} updated successfully", id);
    }

    @Authentication
    public void updateTrainer(String authUsername, String authPassword, String username, String firstName, String lastName, TrainingType specialization, boolean isActive) {
        logger.info("Updating trainer with username: {}", username);
        Trainer trainer = findTrainerByUsername(username);
        trainer.getUser().setFirstName(firstName);
        trainer.getUser().setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);
        logger.debug("Trainer with username: {} updated successfully", username);
    }

    @Authentication
    public Trainer selectTrainer(String authUsername, String authPassword, String username) {
        logger.debug("Selecting trainer with username: {}", username);
        return findTrainerByUsername(username);
    }

    private Trainer findTrainerByUsername(String username) {
        return trainerRepository.findByUser_Username(username).orElseThrow(() -> {
            logger.warn("Trainer with username: {} not found", username);
            return new IllegalArgumentException("Trainer with username " + username + " not found");
        });
    }

    private Trainer findTrainerById(int id) {
        return trainerRepository.findById(id).orElseThrow(() -> {
            logger.warn("Trainer with ID: {} not found", id);
            return new IllegalArgumentException("Trainer with ID " + id + " not found");
        });
    }

    @Authentication
    public List<Trainer> findUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        logger.debug("Finding unassigned trainers");
        List<Trainer> unassignedTrainers = trainerRepository.findTrainerNotInTraineeTrainers(traineeUsername);
        logger.debug("Found {} unassigned trainers", unassignedTrainers.size());
        return unassignedTrainers;
    }

    @Authentication
    public void changePassword(String authUsername, String oldPassword, String newPassword) {
        logger.info("Changing password for user: {}", authUsername);
        Trainer trainer = trainerRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> {
                    logger.warn("Trainer with username: {} not found", authUsername);
                    return new IllegalArgumentException("Trainer with username " + authUsername + " not found");
                });
        if(!passwordEncoder.matches(oldPassword, trainer.getUser().getPassword())) {
            logger.warn("Authentication failed: username or password is invalid");
            throw new AuthenticationException("Username or password is invalid");
        }
        trainer.getUser().setPassword(passwordEncoder.encode(newPassword));
        trainerRepository.save(trainer);
        logger.debug("Password changed successfully for user: {}", authUsername);
    }

    @Authentication
    public void setActiveStatus(String authUsername, String authPassword, String username, boolean isActive) {
        Trainer trainer = findTrainerByUsername(username);
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);
        logger.info("Setting active status for trainer with ID: {} to {}", trainer.getId(), isActive);
    }

    @Authentication
    public void toggleActiveStatus(String authUsername, String authPassword, int id) {
        logger.info("Toggling active status for trainer with ID: {}", id);
        Trainer trainer = findTrainerById(id);
        if(trainer.getUser().isActive()) {
            trainer.getUser().setActive(false);
            logger.debug("Trainer with ID: {} deactivated", id);
        } else {
            trainer.getUser().setActive(true);
            logger.debug("Trainer with ID: {} activated", id);
        }
        trainerRepository.save(trainer);
    }
}
