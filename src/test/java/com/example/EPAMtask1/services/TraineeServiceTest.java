package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserCredentialsGenerator credentialsGenerator;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void createTrainee_shouldCreateSuccessfully() {
        when(credentialsGenerator.generatePassword()).thenReturn("password123");
        when(credentialsGenerator.generateUsername("John", "Doe")).thenReturn("john.doe");

        Trainee trainee = traineeService.createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "Wroclaw");

        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertEquals("john.doe", trainee.getUser().getUsername());
        assertEquals("password123", trainee.getUser().getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("Wroclaw", trainee.getAddress());

        verify(traineeRepository).save(trainee);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void updateTrainee_shouldUpdateSuccessfully() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        traineeService.updateTrainee("auth.user", "authPass", 1, "Updated", "Name",
                LocalDate.of(1991, 2, 2), "New Address");

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals("Updated", trainee.getUser().getFirstName());
        assertEquals("Name", trainee.getUser().getLastName());
        assertEquals(LocalDate.of(1991, 2, 2), trainee.getDateOfBirth());
        assertEquals("New Address", trainee.getAddress());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainee_shouldThrowException_whenNotFound() {
        when(traineeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.updateTrainee("auth.user", "authPass", 99, "X", "Y", LocalDate.now(), "Z"));
    }

    @Test
    void deleteTrainee_shouldDeleteSuccessfully() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        trainee.setTrainers(new ArrayList<>());
        trainee.setUser(user);
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john.doe", "password123");

        verify(authenticationService).authenticate("john.doe", "password123");
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void deleteTrainee_shouldThrowException_whenUsernameNotFound() {
        when(traineeRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.deleteTrainee("unknown", "password123"));
    }

    @Test
    void selectTrainee_shouldReturnTrainee_whenFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTrainee("auth.user", "authPass", "john.doe");

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(trainee, result);
    }

    @Test
    void selectTrainee_shouldThrowException_whenNotFound() {
        when(traineeRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTrainee("auth.user", "authPass", "unknown"));
    }

    @Test
    void changePassword_shouldChangeSuccessfully() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("oldPass");
        trainee.setUser(user);
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.changePassword("john.doe", "oldPass", "newPass");

        verify(authenticationService).authenticate("john.doe", "oldPass");
        assertEquals("newPass", trainee.getUser().getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void toggleActiveStatus_shouldDeactivate_whenCurrentlyActive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(true);
        trainee.setUser(user);
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        traineeService.toggleActiveStatus("auth.user", "authPass", 1);

        assertFalse(trainee.getUser().isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void toggleActiveStatus_shouldActivate_whenCurrentlyInactive() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setActive(false);
        trainee.setUser(user);
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        traineeService.toggleActiveStatus("auth.user", "authPass", 1);

        assertTrue(trainee.getUser().isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTraineeTrainers_shouldReplaceTrainersList() {
        Trainee trainee = new Trainee();
        trainee.setId(1);
        trainee.setTrainers(new ArrayList<>());
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        Trainer newTrainer = new Trainer();
        newTrainer.setId(5);
        newTrainer.setTrainees(new ArrayList<>());
        when(trainerRepository.findById(5)).thenReturn(Optional.of(newTrainer));

        List<Trainer> result = traineeService.updateTraineeTrainers(
                "auth.user", "authPass", 1, List.of(5));

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(1, result.size());
        assertEquals(newTrainer, result.get(0));
        assertTrue(newTrainer.getTrainees().contains(trainee));
        verify(trainerRepository).save(newTrainer);
    }

    @Test
    void updateTraineeTrainers_shouldRemoveTraineeFromOldTrainers() {
        Trainee trainee = new Trainee();
        trainee.setId(1);
        Trainer oldTrainer = new Trainer();
        oldTrainer.setId(2);
        oldTrainer.setTrainees(new ArrayList<>(List.of(trainee)));
        trainee.setTrainers(new ArrayList<>(List.of(oldTrainer)));

        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        List<Trainer> result = traineeService.updateTraineeTrainers(
                "auth.user", "authPass", 1, List.of());

        assertTrue(result.isEmpty());
        assertFalse(oldTrainer.getTrainees().contains(trainee));
        verify(trainerRepository).save(oldTrainer);
    }

    @Test
    void findTraineeById_shouldReturnTrainee_whenFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        assertEquals(trainee, traineeService.findTraineeById(1));
    }

    @Test
    void findTraineeByUsername_shouldReturnTrainee_whenFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        assertEquals(trainee, traineeService.findTraineeByUsername("john.doe"));
    }
}