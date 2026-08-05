package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void createTraining_shouldCreateSuccessfully() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");

        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));

        Training training = trainingService.createTraining("auth.user", "authPass", 1, 1,
                "Morning Cardio", cardio, LocalDate.of(2024, 6, 15), 60);

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(cardio, training.getTrainingType());
        assertEquals(LocalDate.of(2024, 6, 15), training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());

        verify(trainingRepository).save(training);
    }

    @Test
    void createTraining_shouldThrowException_whenTraineeNotFound() {
        when(traineeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining("auth.user", "authPass", 99, 1,
                        "Test", new TrainingType(), LocalDate.now(), 60));

        verifyNoInteractions(trainingRepository);
    }

    @Test
    void createTraining_shouldThrowException_whenTrainerNotFound() {
        when(traineeRepository.findById(1)).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining("auth.user", "authPass", 1, 99,
                        "Test", new TrainingType(), LocalDate.now(), 60));

        verifyNoInteractions(trainingRepository);
    }

    @Test
    void selectTraining_shouldReturnTraining_whenFound() {
        Training training = new Training();
        when(trainingRepository.findById(1)).thenReturn(Optional.of(training));

        Training result = trainingService.selectTraining("auth.user", "authPass", 1);

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(training, result);
    }

    @Test
    void selectTraining_shouldThrowException_whenNotFound() {
        when(trainingRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.selectTraining("auth.user", "authPass", 99));
    }

    @Test
    void getTraineeTrainingsByCriteria_shouldReturnMatchingList() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));
        Training training = new Training();
        when(trainingRepository.findByTraineeUsernameAndDateAndTrainerAndType(
                "john.doe", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), "Jane", "CARDIO"))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainingsByCriteria(
                "auth.user", "authPass", "john.doe",
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), "Jane", "CARDIO");

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(1, result.size());
        assertEquals(training, result.get(0));
    }

    @Test
    void getTrainerTrainingsByCriteria_shouldReturnMatchingList() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(trainer));
        Training training = new Training();
        when(trainingRepository.findByTrainerUserUsernameAndDateAndTrainee(
                "jane.smith", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), "John"))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTrainerTrainingsByCriteria(
                "auth.user", "authPass", "jane.smith",
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), "John");

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(1, result.size());
        assertEquals(training, result.get(0));
    }
}