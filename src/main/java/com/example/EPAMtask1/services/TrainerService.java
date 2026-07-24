package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private UserCredentialsGenerator credentialsGenerator;

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        Trainer trainer = new Trainer();
        int userId = trainerDao.generateNextId();

        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setPassword(credentialsGenerator.generatePassword());
        trainer.setUserId(userId);
        trainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName));

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
}
