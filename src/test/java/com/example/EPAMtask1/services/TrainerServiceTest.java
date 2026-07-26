package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private UserCredentialsGenerator credentialsGenerator;
    @InjectMocks
    private TrainerService trainerService;

    @Test
    void createTrainer_ShouldCreateTrainerSuccessfully() {
        when(trainerDao.generateNextId()).thenReturn(1);
        when(credentialsGenerator.generatePassword()).thenReturn("password123");
        when(credentialsGenerator.generateUsername("Test", "Testovich")).thenReturn("test.testovich");

        Trainer trainer = trainerService.createTrainer("Test", "Testovich", TrainingType.STRENGTH);

        assertEquals("Test", trainer.getFirstName());
        assertEquals("Testovich", trainer.getLastName());
        assertEquals(TrainingType.STRENGTH, trainer.getSpecialization());
        assertEquals("test.testovich", trainer.getUsername());
        assertEquals("password123", trainer.getPassword());

        verify(trainerDao).createTrainer(trainer);
    }

    @Test
    void selectTrainer_shouldReturnTrainer_whenFound() {
        Trainer trainer = new Trainer();
        when(trainerDao.selectTrainer(1)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainer(1);

        assertEquals(trainer, result);
    }

    @Test
    void selectTrainer_shouldThrowException_whenNotFound() {
        when(trainerDao.selectTrainer(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainerService.selectTrainer(99));
    }

    @Test
    void updateTrainer_shouldUpdateTrainerSuccessfully() {
        Trainer trainer = new Trainer();
        when(trainerDao.selectTrainer(1)).thenReturn(Optional.of(trainer));

        trainerService.updateTrainer(1, "Updated", "Name", TrainingType.YOGA);

        assertEquals("Updated", trainer.getFirstName());
        assertEquals("Name", trainer.getLastName());
        assertEquals(TrainingType.YOGA, trainer.getSpecialization());

        verify(trainerDao).updateTrainer(trainer);
    }
}
