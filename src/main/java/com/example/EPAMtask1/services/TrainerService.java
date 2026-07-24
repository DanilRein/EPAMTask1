package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrainerService {
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private TraineeDao traineeDao;

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        Trainer trainer = new Trainer();
        int userId = trainerDao.generateNextId();

        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);

        String password = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        trainer.setPassword(password);

        trainer.setUserId(userId);

        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int suffix = 1;
        while (isUsernameTaken(username)) {
            username = baseUsername + suffix++;
        }

        trainer.setUsername(username);

        trainerDao.createTrainer(trainer);
        return trainer;
    }

    public void updateTrainer(int id, String firstName, String lastName, String specialization) {
        Trainer trainer = selectTrainer(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainerDao.updateTrainer(trainer);
    }

    public Trainer selectTrainer(int id) {
        return trainerDao.selectTrainer(id).orElseThrow(() -> new IllegalArgumentException("Trainer with ID " + id + " not found"));
    }

    private boolean isUsernameTaken(String username) {
        boolean inTrainers = trainerDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        boolean inTrainees = traineeDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        return inTrainers || inTrainees;
    }
}
