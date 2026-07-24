package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TraineeService {
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        int userId = traineeDao.generateNextId();

        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setPassword(credentialsGenerator.generatePassword());
        trainee.setUserId(userId);
        trainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName));

        traineeDao.createTrainee(trainee);
        return trainee;
    }

    public void updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = selectTrainee(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        traineeDao.updateTrainee(trainee);
    }

    public void deleteTrainee(int id) {
        selectTrainee(id);
        traineeDao.deleteTrainee(id);
    }

    public Trainee selectTrainee(int id) {
        return traineeDao.selectTrainee(id).orElseThrow(() -> new IllegalArgumentException("Trainee with ID " + id + " not found"));
    }
}
