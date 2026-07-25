package com.example.EPAMtask1.facade;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.services.TraineeService;
import com.example.EPAMtask1.services.TrainerService;
import com.example.EPAMtask1.services.TrainingService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }
    public void updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        traineeService.updateTrainee(id, firstName, lastName, dateOfBirth, address);
    }
    public void deleteTrainee(int id) {
        traineeService.deleteTrainee(id);
    }
    public Trainee selectTrainee(int id) {
        return traineeService.selectTrainee(id);
    }

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }
    public void updateTrainer(int id, String firstName, String lastName, String specialization) {
        trainerService.updateTrainer(id, firstName, lastName, specialization);
    }
    public Trainer selectTrainer(int id) {
        return trainerService.selectTrainer(id);
    }

    public Training createTraining(int traineeId, int trainerId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        return trainingService.createTraining(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
    }
    public Training selectTraining(int trainingId) {
        return trainingService.selectTraining(trainingId);
    }
}
