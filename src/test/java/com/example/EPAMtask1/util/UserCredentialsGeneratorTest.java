package com.example.EPAMtask1.util;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCredentialsGeneratorTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private UserCredentialsGenerator credentialsGenerator;

    private Trainee traineeWithName(String firstName, String lastName) {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainee.setUser(user);
        return trainee;
    }

    private Trainer trainerWithName(String firstName, String lastName) {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        trainer.setUser(user);
        return trainer;
    }

    @Test
    void generateUsername_shouldReturnBaseUsername_whenNoCollision() {
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe", username);
    }

    @Test
    void generateUsername_shouldAddSuffix_whenCollisionWithTrainee() {
        when(traineeRepository.findAll()).thenReturn(List.of(traineeWithName("John", "Doe")));
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
    }

    @Test
    void generateUsername_shouldAddSuffix_whenCollisionWithTrainer() {
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());
        when(trainerRepository.findAll()).thenReturn(List.of(trainerWithName("John", "Doe")));

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
    }

    @Test
    void generateUsername_shouldSumCollisions_acrossTraineesAndTrainers() {
        when(traineeRepository.findAll()).thenReturn(List.of(traineeWithName("John", "Doe")));
        when(trainerRepository.findAll()).thenReturn(List.of(trainerWithName("John", "Doe")));

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe2", username);
    }

    @Test
    void generatePassword_shouldReturnTenCharacterString() {
        String password = credentialsGenerator.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }
}