package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TraineeService {
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private TrainerDao trainerDao;

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        int userId = traineeDao.generateNextId();

        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        String password = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        trainee.setPassword(password);

        trainee.setUserId(userId);

        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int suffix=1;
        while (isUsernameTaken(username)) {
            username = baseUsername + suffix++;
        }

        trainee.setUsername(username);

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
        Trainee trainee = selectTrainee(id);
        traineeDao.deleteTrainee(id);
    }
    public Trainee selectTrainee(int id) {
        return traineeDao.selectTrainee(id).orElseThrow(() -> new IllegalArgumentException("Trainee with ID " + id + " not found"));
    }

    private boolean isUsernameTaken(String username) {
        boolean inTrainees = traineeDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        boolean inTrainers = trainerDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        return inTrainees || inTrainers;
    }

}
