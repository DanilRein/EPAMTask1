package com.example.EPAMtask1.dao;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.storage.TraineeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class TraineeDao {
    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }
    public void createTrainee(Trainee trainee) {
        traineeStorage.getStorage().put(trainee.getUserId(), trainee);
        logger.debug("Created trainee with ID: {}", trainee.getUserId());
    }
    public void updateTrainee(Trainee trainee) {
        traineeStorage.getStorage().put(trainee.getUserId(), trainee);
        logger.debug("Updated trainee with ID: {}", trainee.getUserId());
    }
    public void deleteTrainee(int userId) {
        traineeStorage.getStorage().remove(userId);
        logger.debug("Deleted trainee with ID: {}", userId);
    }
    public Optional<Trainee> selectTrainee(int userId) {
        Optional<Trainee> trainee = Optional.ofNullable(traineeStorage.getStorage().get(userId));
        trainee.ifPresentOrElse(
            t -> logger.debug("Selected trainee with ID: {}", t.getUserId()),
            () -> logger.debug("Trainee with ID: {} not found", userId)
        );
        return trainee;
    }
}
