package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
    private final TraineeDao traineeDao;

    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainee(String firstName, String lastName, String password) {


    }
    public void updateTrainee(int id, String firstName, String lastName) {
        // Implementation for updating a trainee
    }
    public void deleteTrainee(int id) {
        // Implementation for deleting a trainee
    }
    public void selectTrainee(int id) {
        // Implementation for selecting a trainee
    }
}
