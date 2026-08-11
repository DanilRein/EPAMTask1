package com.example.EPAMtask1.util;


import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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

    @Test
    void generateUsername_shouldReturnBaseUsername_whenNoCollision() {
        when(traineeRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(0L);
        when(trainerRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(0L);

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe", username);
    }

    @Test
    void generateUsername_shouldAddSuffix_whenCollisionWithTrainee() {
        when(traineeRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(1L);
        when(trainerRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(0L);

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
    }

    @Test
    void generateUsername_shouldAddSuffix_whenCollisionWithTrainer() {
        when(traineeRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(0L);
        when(trainerRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(1L);

        String username = credentialsGenerator.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
    }

    @Test
    void generateUsername_shouldSumCollisions_acrossTraineesAndTrainers() {
        when(traineeRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(1L);
        when(trainerRepository.countByUser_FirstNameAndUser_LastName("John", "Doe")).thenReturn(1L);

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