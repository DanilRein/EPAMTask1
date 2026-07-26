package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.dao.TrainingDao;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TrainingDao trainingDao;

    public Training createTraining(int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        logger.info(
                "Creating training with traineeId: {}, trainerId: {}, trainingName: {}, trainingType: {}, trainingDate: {}, trainingDuration: {}",
                traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration
        );
        traineeDao.selectTrainee(traineeId).orElseThrow(() -> {
            logger.warn("Trainee with ID: {} not found", traineeId);
            return new IllegalArgumentException("Trainee with ID " + traineeId + " not found");
        });
        trainerDao.selectTrainer(trainerId).orElseThrow(() -> {
            logger.warn("Trainer with ID: {} not found", trainerId);
            return new IllegalArgumentException("Trainer with ID " + trainerId + " not found");
        });
        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        training.setTrainingId(trainingDao.generateNextId());
        trainingDao.createTraining(training);
        logger.debug("Training created successfully for traineeId: {} and trainerId: {}", traineeId, trainerId);
        return training;
    }

    public Training selectTraining(int trainingId) {
        logger.debug("Selecting training with ID: {}", trainingId);
        return trainingDao.selectTraining(trainingId).orElseThrow(() -> {
            logger.warn("Training with ID: {} not found", trainingId);
            return new IllegalArgumentException("Training with ID " + trainingId + " not found");
        });
    }
}