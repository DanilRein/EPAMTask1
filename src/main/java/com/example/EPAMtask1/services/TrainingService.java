package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.dao.TrainingDao;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingService {
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TrainingDao trainingDao;

    public Training createTraining(int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        trainingDao.createTraining(training);
        return training;
    }
    public Training selectTraining(int trainingId) {
        // Additional business logic can be added here
        return trainingDao.selectTraining(trainingId).orElseThrow(() -> new IllegalArgumentException("Training with ID " + trainingId + " not found"));
    }
}
