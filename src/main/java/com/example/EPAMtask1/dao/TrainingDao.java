package com.example.EPAMtask1.dao;

import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.storage.TrainingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class TrainingDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDao.class);
    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public void createTraining(Training training) {
        trainingStorage.getStorage().put(training.getTrainingId(), training);
        logger.debug("Created training with ID: {}", training.getTrainingId());
    }

    public Optional<Training> selectTraining(int trainingId) {
        Optional<Training> training = Optional.ofNullable(trainingStorage.getStorage().get(trainingId));
        training.ifPresentOrElse(
            t -> logger.debug("Selected training with ID: {}", t.getTrainingId()),
            () -> logger.debug("Training with ID: {} not found", trainingId)
        );
        return training;
    }
}
