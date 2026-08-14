package com.example.EPAMtask1.facade;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.services.TraineeService;
import com.example.EPAMtask1.services.TrainerService;
import com.example.EPAMtask1.services.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    public GymFacade (TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    //Trainee methods

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }
    public void updateTrainee(String authUsername, String authPassword, int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        traineeService.updateTrainee(authUsername, authPassword, id, firstName, lastName, dateOfBirth, address);
    }
    public void updateTraineeByUsername(String authUsername, String authPassword, String username, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
        traineeService.updateTrainee(authUsername, authPassword, username, firstName, lastName, dateOfBirth, address, isActive);
    }
    public void deleteTrainee(String authUsername, String authPassword) {
        traineeService.deleteTrainee(authUsername, authPassword);
    }
    public Trainee selectTrainee(String authUsername, String authPassword, String username) {
        return traineeService.selectTrainee(authUsername, authPassword, username);
    }
    public List<Trainer> updateTraineeTrainers(String authUsername, String authPassword, int traineeId, List<Integer> trainerIds) {
        return traineeService.updateTraineeTrainers(authUsername, authPassword, traineeId, trainerIds);
    }
    public void changeTraineePassword(String authUsername, String oldPassword, String newPassword) {
        traineeService.changePassword(authUsername, oldPassword, newPassword);
    }
    public void toggleTraineeActiveStatus(String authUsername, String authPassword, int id) {
        traineeService.toggleActiveStatus(authUsername, authPassword, id);
    }

    //Trainer methods

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }
    public void updateTrainer(String authUsername, String authPassword, int id, String firstName, String lastName, TrainingType specialization) {
        trainerService.updateTrainer(authUsername, authPassword, id, firstName, lastName, specialization);
    }
    public void updateTrainerByUsername(String authUsername, String authPassword, String username, String firstName, String lastName, TrainingType specialization, boolean isActive) {
        trainerService.updateTrainer(authUsername, authPassword, username, firstName, lastName, specialization, isActive);
    }
    public Trainer selectTrainer(String authUsername, String authPassword, String username) {
        return trainerService.selectTrainer(authUsername, authPassword, username);
    }
    public List<Trainer> findUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        return trainerService.findUnassignedTrainers(authUsername, authPassword, traineeUsername);
    }
    public void changeTrainerPassword(String authUsername, String oldPassword, String newPassword) {
        trainerService.changePassword(authUsername, oldPassword, newPassword);
    }
    public void toggleTrainerActiveStatus(String authUsername, String authPassword, int id) {
        trainerService.toggleActiveStatus(authUsername, authPassword, id);
    }

    //Training methods

    public Training createTraining(String authUsername, String authPassword, int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        return trainingService.createTraining(authUsername, authPassword, traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
    }
    public Training selectTraining(String authUsername, String authPassword, int trainingId) {
        return trainingService.selectTraining(authUsername, authPassword, trainingId);
    }
    public List<Training> getTraineeTrainingsByCriteria(String authUsername, String authPassword, String traineeUsername, LocalDate startDate, LocalDate endDate, String trainerName, String trainingTypeName) {
        return trainingService.getTraineeTrainingsByCriteria(authUsername, authPassword, traineeUsername, startDate, endDate, trainerName, trainingTypeName);
    }
    public List<Training> getTrainerTrainingsByCriteria(String authUsername, String authPassword, String trainerUsername, LocalDate startDate, LocalDate endDate, String traineeName) {
        return trainingService.getTrainerTrainingsByCriteria(authUsername, authPassword, trainerUsername, startDate, endDate, traineeName);
    }
}
