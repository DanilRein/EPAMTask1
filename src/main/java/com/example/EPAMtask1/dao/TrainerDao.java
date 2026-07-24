package com.example.EPAMtask1.dao;

import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.storage.TrainerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    public void createTrainer(Trainer trainer) {
        trainerStorage.getStorage().put(trainer.getUserId(), trainer);
        logger.debug("Created trainer with ID: {}", trainer.getUserId());
    }

    public void updateTrainer(Trainer trainer) {
        trainerStorage.getStorage().put(trainer.getUserId(), trainer);
        logger.debug("Updated trainer with ID: {}", trainer.getUserId());
    }

    public Optional<Trainer> selectTrainer(int userId) {
        Optional<Trainer> trainer = Optional.ofNullable(trainerStorage.getStorage().get(userId));
        trainer.ifPresentOrElse(
            t -> logger.debug("Selected trainer with ID: {}", t.getUserId()),
            () -> logger.debug("Trainer with ID: {} not found", userId)
        );
        return trainer;
    }
    public List<Trainer> findAll() {
        List<Trainer> trainers = new ArrayList<>(trainerStorage.getStorage().values());
        logger.debug("Retrieved all trainers, count: {}", trainers.size());
        return trainers;
    }
    public int generateNextId() {
        return trainerStorage.nextId();
    }
}
