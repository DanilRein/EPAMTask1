package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        logger.info("Creating trainer with firstName: {}, lastName: {}, specialization: {}", firstName, lastName, specialization);
        Trainer trainer = new Trainer();
        int userId = trainerDao.generateNextId();

        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setPassword(credentialsGenerator.generatePassword());
        trainer.setUserId(userId);
        trainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName));

        trainerDao.createTrainer(trainer);
        logger.debug("Trainer created successfully with ID: {} and username: {}", trainer.getUserId(), trainer.getUsername());
        return trainer;
    }

    public void updateTrainer(int id, String firstName, String lastName, String specialization) {
        logger.info("Updating trainer with ID: {}", id);
        Trainer trainer = selectTrainer(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainerDao.updateTrainer(trainer);
        logger.debug("Trainer with ID: {} updated successfully", id);
    }

    public Trainer selectTrainer(int id) {
        logger.debug("Selecting trainer with ID: {}", id);
        return trainerDao.selectTrainer(id).orElseThrow(() -> {
            logger.error("Trainer with ID: {} not found", id);
            return new IllegalArgumentException("Trainer with ID " + id + " not found");
        });
    }
}
