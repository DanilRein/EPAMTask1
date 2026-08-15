package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.repository.TrainingRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final AuthenticationService authenticationService;

    public Training createTraining(String authUsername, String authPassword, int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info(
                "Creating training with traineeId: {}, trainerId: {}, trainingName: {}, trainingType: {}, trainingDate: {}, trainingDuration: {}",
                traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration
        );
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() -> {
            logger.warn("Trainee with ID: {} not found", traineeId);
            return new IllegalArgumentException("Trainee with ID " + traineeId + " not found");
        });
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> {
            logger.warn("Trainer with ID: {} not found", trainerId);
            return new IllegalArgumentException("Trainer with ID " + trainerId + " not found");
        });
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        trainingRepository.save(training);
        logger.debug("Training created successfully for traineeId: {} and trainerId: {}", traineeId, trainerId);
        return training;
    }
    public Training createTrainingByUsernames(String authUsername, String authPassword, String traineeUsername, String trainerUsername, String trainingName, LocalDate trainingDate, int trainingDuration) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info(
                "Creating training with traineeUsername: {}, trainerUsername: {}, trainingName: {}, trainingDate: {}, trainingDuration: {}",
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration
        );
        Trainee trainee = traineeRepository.findByUser_Username(traineeUsername).orElseThrow(() -> {
            logger.warn("Trainee with username: {} not found", traineeUsername);
            return new IllegalArgumentException("Trainee with username " + traineeUsername + " not found");
        });
        Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElseThrow(() -> {
            logger.warn("Trainer with username: {} not found", trainerUsername);
            return new IllegalArgumentException("Trainer with username " + trainerUsername + " not found");
        });
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainer.getSpecialization());
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        trainingRepository.save(training);
        logger.debug("Training created successfully for traineeUsername: {} and trainerUsername: {}", traineeUsername, trainerUsername);
        return training;
    }

    public Training selectTraining(String authUsername, String authPassword, int trainingId) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.debug("Selecting training with ID: {}", trainingId);
        return trainingRepository.findById(trainingId).orElseThrow(() -> {
            logger.warn("Training with ID: {} not found", trainingId);
            return new IllegalArgumentException("Training with ID " + trainingId + " not found");
        });
    }

    public List<Training> getTraineeTrainingsByCriteria(String authUsername, String authPassword, String traineeUsername, LocalDate startDate, LocalDate endDate, String trainerName, String trainingTypeName) {
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Getting trainings for traineeUsername: {} with trainingType: {}, startDate: {}, endDate: {}, trainerName: {}, trainingTypeName: {}", traineeUsername, trainingTypeName, startDate, endDate, trainerName, trainingTypeName);
        List<Training> trainings = trainingRepository.findByTraineeUsernameAndDateAndTrainerAndType(traineeUsername, startDate, endDate, trainerName, trainingTypeName);
        logger.debug("Found {} trainings for traineeUsername: {}", trainings.size(), traineeUsername);
        return trainings;
    }

    public List<Training> getTrainerTrainingsByCriteria(String authUsername, String authPassword, String trainerUsername, LocalDate startDate, LocalDate endDate, String traineeName){
        authenticationService.authenticate(authUsername, authPassword);
        logger.info("Getting trainings for trainerUsername: {} with startDate: {}, endDate: {}, traineeName: {}", trainerUsername, startDate, endDate, traineeName);
        List<Training> trainings = trainingRepository.findByTrainerUserUsernameAndDateAndTrainee(trainerUsername, startDate, endDate, traineeName);
        logger.debug("Found {} trainings for trainerUsername: {}", trainings.size(), trainerUsername);
        return trainings;
    }
}