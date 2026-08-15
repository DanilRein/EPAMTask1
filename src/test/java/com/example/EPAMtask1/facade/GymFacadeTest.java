package com.example.EPAMtask1.facade;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.services.TraineeService;
import com.example.EPAMtask1.services.TrainerService;
import com.example.EPAMtask1.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void createTrainee_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "Wroclaw"))
                .thenReturn(trainee);

        Trainee result = gymFacade.createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "Wroclaw");

        assertEquals(trainee, result);
        verify(traineeService).createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "Wroclaw");
    }

    @Test
    void updateTraineeByUsername_shouldDelegateToTraineeService() {
        gymFacade.updateTraineeByUsername("auth", "pass", "john.doe", "John", "Doe",
                LocalDate.of(1990, 1, 1), "Wroclaw", true);

        verify(traineeService).updateTrainee("auth", "pass", "john.doe", "John", "Doe",
                LocalDate.of(1990, 1, 1), "Wroclaw", true);
    }

    @Test
    void deleteTrainee_shouldDelegateToTraineeService() {
        gymFacade.deleteTrainee("john.doe", "pass");

        verify(traineeService).deleteTrainee("john.doe", "pass");
    }

    @Test
    void selectTrainee_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.selectTrainee("auth", "pass", "john.doe")).thenReturn(trainee);

        Trainee result = gymFacade.selectTrainee("auth", "pass", "john.doe");

        assertEquals(trainee, result);
        verify(traineeService).selectTrainee("auth", "pass", "john.doe");
    }

    @Test
    void changeTraineePassword_shouldDelegateToTraineeService() {
        gymFacade.changeTraineePassword("john.doe", "oldPass", "newPass");

        verify(traineeService).changePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void setTraineeActiveStatus_shouldDelegateToTraineeService() {
        gymFacade.setTraineeActiveStatus("auth", "pass", "john.doe", false);

        verify(traineeService).setActiveStatus("auth", "pass", "john.doe", false);
    }

    @Test
    void updateTraineeTrainersByUsername_shouldDelegateToTraineeService() {
        Trainer trainer = new Trainer();
        when(traineeService.updateTraineeTrainersByUsername("auth", "pass", "john.doe", List.of("jane.smith")))
                .thenReturn(List.of(trainer));

        List<Trainer> result = gymFacade.updateTraineeTrainersByUsername("auth", "pass", "john.doe", List.of("jane.smith"));

        assertEquals(List.of(trainer), result);
        verify(traineeService).updateTraineeTrainersByUsername("auth", "pass", "john.doe", List.of("jane.smith"));
    }

    @Test
    void createTrainer_shouldDelegateToTrainerService() {
        TrainingType type = new TrainingType();
        Trainer trainer = new Trainer();
        when(trainerService.createTrainer("Jane", "Smith", type)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer("Jane", "Smith", type);

        assertEquals(trainer, result);
        verify(trainerService).createTrainer("Jane", "Smith", type);
    }

    @Test
    void updateTrainerByUsername_shouldDelegateToTrainerService() {
        TrainingType type = new TrainingType();
        gymFacade.updateTrainerByUsername("auth", "pass", "jane.smith", "Jane", "Smith", type, true);

        verify(trainerService).updateTrainer("auth", "pass", "jane.smith", "Jane", "Smith", type, true);
    }

    @Test
    void selectTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.selectTrainer("auth", "pass", "jane.smith")).thenReturn(trainer);

        Trainer result = gymFacade.selectTrainer("auth", "pass", "jane.smith");

        assertEquals(trainer, result);
        verify(trainerService).selectTrainer("auth", "pass", "jane.smith");
    }

    @Test
    void changeTrainerPassword_shouldDelegateToTrainerService() {
        gymFacade.changeTrainerPassword("jane.smith", "oldPass", "newPass");

        verify(trainerService).changePassword("jane.smith", "oldPass", "newPass");
    }

    @Test
    void setTrainerActiveStatus_shouldDelegateToTrainerService() {
        gymFacade.setTrainerActiveStatus("auth", "pass", "jane.smith", false);

        verify(trainerService).setActiveStatus("auth", "pass", "jane.smith", false);
    }

    @Test
    void findUnassignedTrainers_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.findUnassignedTrainers("auth", "pass", "john.doe")).thenReturn(List.of(trainer));

        List<Trainer> result = gymFacade.findUnassignedTrainers("auth", "pass", "john.doe");

        assertEquals(List.of(trainer), result);
        verify(trainerService).findUnassignedTrainers("auth", "pass", "john.doe");
    }

    @Test
    void createTrainingByUsernames_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.createTrainingByUsernames("auth", "pass", "john.doe", "jane.smith",
                "Morning Cardio", LocalDate.of(2024, 6, 15), 60)).thenReturn(training);

        Training result = gymFacade.createTrainingByUsernames("auth", "pass", "john.doe", "jane.smith",
                "Morning Cardio", LocalDate.of(2024, 6, 15), 60);

        assertEquals(training, result);
        verify(trainingService).createTrainingByUsernames("auth", "pass", "john.doe", "jane.smith",
                "Morning Cardio", LocalDate.of(2024, 6, 15), 60);
    }

    @Test
    void selectTraining_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.selectTraining("auth", "pass", 1)).thenReturn(training);

        Training result = gymFacade.selectTraining("auth", "pass", 1);

        assertEquals(training, result);
        verify(trainingService).selectTraining("auth", "pass", 1);
    }

    @Test
    void getTraineeTrainingsByCriteria_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.getTraineeTrainingsByCriteria("auth", "pass", "john.doe",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Jane", "CARDIO"))
                .thenReturn(List.of(training));

        List<Training> result = gymFacade.getTraineeTrainingsByCriteria("auth", "pass", "john.doe",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Jane", "CARDIO");

        assertEquals(List.of(training), result);
    }

    @Test
    void getTrainerTrainingsByCriteria_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.getTrainerTrainingsByCriteria("auth", "pass", "jane.smith",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "John"))
                .thenReturn(List.of(training));

        List<Training> result = gymFacade.getTrainerTrainingsByCriteria("auth", "pass", "jane.smith",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "John");

        assertEquals(List.of(training), result);
    }
}