package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;
    @Autowired
    private AuthenticationService authenticationService;

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        logger.info("Creating trainer with firstName: {}, lastName: {}, specialization: {}", firstName, lastName, specialization);
        User user = new User();
        Trainer trainer = new Trainer();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainer.setSpecialization(specialization);
        user.setPassword(credentialsGenerator.generatePassword());
        user.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainer.setUser(user);
        trainerRepository.save(trainer);
        logger.debug("Trainer created successfully with ID: {} and username: {}", trainer.getId(), trainer.getUser().getUsername());
        return trainer;
    }

    public void updateTrainer(String authUsername, String authPassword, int id, String firstName, String lastName, TrainingType specialization) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Updating trainer with ID: {}", id);
        Trainer trainer = findTrainerById(id);
        trainer.getUser().setFirstName(firstName);
        trainer.getUser().setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainerRepository.save(trainer);
        logger.debug("Trainer with ID: {} updated successfully", id);
    }

    public Trainer selectTrainer(String authUsername, String authPassword, int id) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.debug("Selecting trainer with ID: {}", id);
        return findTrainerById(id);
    }

    private Trainer findTrainerById(int id) {
        return trainerRepository.findById(id).orElseThrow(() -> {
            logger.warn("Trainer with ID: {} not found", id);
            return new IllegalArgumentException("Trainer with ID " + id + " not found");
        });
    }

    public void changePassword(String authUsername, String oldPassword, String newPassword) {
        authenticationService.authenticate(authUsername, oldPassword);
        logger.info("Changing password for user: {}", authUsername);
        Trainer trainer = trainerRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> {
                    logger.warn("Trainer with username: {} not found", authUsername);
                    return new IllegalArgumentException("Trainer with username " + authUsername + " not found");
                });
        trainer.getUser().setPassword(newPassword);
        trainerRepository.save(trainer);
        logger.debug("Password changed successfully for user: {}", authUsername);
    }
    public void toggleActiveStatus(String authUsername, String authPassword, int id) {
        authenticationService.authenticate(authUsername, authPassword);
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
