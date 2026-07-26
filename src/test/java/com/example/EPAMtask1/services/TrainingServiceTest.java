package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.dao.TrainerDao;
import com.example.EPAMtask1.dao.TrainingDao;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
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
public class TrainingServiceTest {
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainingDao trainingDao;
    @InjectMocks
    private TrainingService trainingService;

    @Test
    void createTraining_ShouldCreateTrainingSuccessfully() {
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        when(trainerDao.selectTrainer(1)).thenReturn(Optional.of(trainer));
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.of(trainee));
        Training training = trainingService.createTraining(1, 1, "Test Training", TrainingType.CARDIO, null, 60);
        assertEquals(1, training.getTraineeId());
        assertEquals(1, training.getTrainerId());
        assertEquals("Test Training", training.getTrainingName());
        assertEquals(TrainingType.CARDIO, training.getTrainingType());
        assertEquals(60, training.getTrainingDuration());
        verify(trainingDao).createTraining(training);
    }

    @Test
    void createTraining_ShouldThrowException_WhenTraineeNotFound() {
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(1, 1, "Test Training", TrainingType.CARDIO, null, 60));
    }

    @Test
    void createTraining_ShouldThrowException_WhenTrainerNotFound() {
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.selectTrainer(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(1, 1, "Test Training", TrainingType.CARDIO, null, 60));
    }
    @Test
    void selectTraining_shouldReturnTraining_whenFound() {
        Training training = new Training();
        when(trainingDao.selectTraining(1)).thenReturn(Optional.of(training));
        Training result = trainingService.selectTraining(1);
        assertEquals(training, result);
    }
    @Test
    void selectTraining_shouldThrowException_whenNotFound() {
        when(trainingDao.selectTraining(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> trainingService.selectTraining(99));
    }

}

