package com.example.EPAMtask1.util;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCredentialsGeneratorTest {

    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @InjectMocks
    private UserCredentialsGenerator credentialsGenerator;

    @Test
    void generateUsername_ShouldGenerateUsername() {
        String firstName = "John";
        String lastName = "Doe";

        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        String username = credentialsGenerator.generateUsername(firstName, lastName);

        assertEquals("john.doe", username);
    }
    @Test
    void generateUsername_ShouldGenerateUniqueUsername_WhenUsernameExists() {
        String firstName = "John";
        String lastName = "Doe";

        Trainee existingTrainee = new Trainee();
        existingTrainee.setUsername("john.doe");
        List<Trainee> trainees = List.of(existingTrainee);

        when(traineeDao.findAll()).thenReturn(trainees);
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String username = credentialsGenerator.generateUsername(firstName, lastName);

        assertEquals("john.doe1", username);
    }
    @Test
    void generateUsername_ShouldGenerateThirdUniqueUsername_WhenSecondUsernameExists() {
        String firstName = "John";
        String lastName = "Doe";

        Trainee existingTrainee = new Trainee();
        Trainee existingTrainee1 = new Trainee();
        existingTrainee.setUsername("john.doe");
        existingTrainee1.setUsername("john.doe1");
        List<Trainee> trainees = List.of(existingTrainee, existingTrainee1);

        when(traineeDao.findAll()).thenReturn(trainees);
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String username = credentialsGenerator.generateUsername(firstName, lastName);

        assertEquals("john.doe2", username);
    }

    @Test
    void generateUsername_ShouldAddSuffix_WhenCollisionWithTrainerExists() {
        Trainer existingTrainer = new Trainer();
        existingTrainer.setUsername("john.doe");

        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.singletonList(existingTrainer));

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
    }
    @Test
    void generatePassword_ShouldReturnTenCharacterString() {
        String password = credentialsGenerator.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }
}
