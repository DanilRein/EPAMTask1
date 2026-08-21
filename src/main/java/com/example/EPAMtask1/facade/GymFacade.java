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
    public void updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        traineeService.updateTrainee(id, firstName, lastName, dateOfBirth, address);
    }
    public void updateTraineeByUsername(String username, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
        traineeService.updateTrainee(username, firstName, lastName, dateOfBirth, address, isActive);
    }
    public void deleteTrainee() {
        traineeService.deleteTrainee();
    }
    public Trainee selectTrainee(String username) {
        return traineeService.selectTrainee(username);
    }
    public List<Trainer> updateTraineeTrainers(int traineeId, List<Integer> trainerIds) {
        return traineeService.updateTraineeTrainers(traineeId, trainerIds);
    }
    public List<Trainer> updateTraineeTrainersByUsername(String traineeUsername, List<String> trainerUsernames) {
        return traineeService.updateTraineeTrainersByUsername(traineeUsername, trainerUsernames);
    }
    public void changeTraineePassword(String authUsername, String oldPassword, String newPassword) {
        traineeService.changePassword(authUsername, oldPassword, newPassword);
    }
    public void toggleTraineeActiveStatus(int id) {
        traineeService.toggleActiveStatus(id);
    }

    //Trainer methods

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }
    public void updateTrainer(int id, String firstName, String lastName, TrainingType specialization) {
        trainerService.updateTrainer(id, firstName, lastName, specialization);
    }
    public void updateTrainerByUsername(String username, String firstName, String lastName, TrainingType specialization, boolean isActive) {
        trainerService.updateTrainer(username, firstName, lastName, specialization, isActive);
    }
    public Trainer selectTrainer(String username) {
        return trainerService.selectTrainer(username);
    }
    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        return trainerService.findUnassignedTrainers(traineeUsername);
    }
    public void changeTrainerPassword(String authUsername, String oldPassword, String newPassword) {
        trainerService.changePassword(authUsername, oldPassword, newPassword);
    }
    public void toggleTrainerActiveStatus(int id) {
        trainerService.toggleActiveStatus(id);
    }

    //Training methods

    public Training createTraining(int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        return trainingService.createTraining(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
    }
    public Training createTrainingByUsernames(String traineeUsername, String trainerUsername, String trainingName, LocalDate trainingDate, int trainingDuration) {
        return trainingService.createTrainingByUsernames(traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);
    }
    public Training selectTraining(int trainingId) {
        return trainingService.selectTraining(trainingId);
    }
    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername, LocalDate startDate, LocalDate endDate, String trainerName, String trainingTypeName) {
        return trainingService.getTraineeTrainingsByCriteria(traineeUsername, startDate, endDate, trainerName, trainingTypeName);
    }
    public List<Training> getTrainerTrainingsByCriteria(String trainerUsername, LocalDate startDate, LocalDate endDate, String traineeName) {
        return trainingService.getTrainerTrainingsByCriteria(trainerUsername, startDate, endDate, traineeName);
    }
    public void setTrainerActiveStatus(String username, boolean isActive) {
        trainerService.setActiveStatus(username, isActive);
    }
    public void setTraineeActiveStatus(String username, boolean isActive) {
        traineeService.setActiveStatus(username, isActive);
    }
}
