package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserCredentialsGenerator credentialsGenerator;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void createTrainer_shouldCreateSuccessfully() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");
        when(credentialsGenerator.generatePassword()).thenReturn("password123");
        when(credentialsGenerator.generateUsername("Jane", "Smith")).thenReturn("jane.smith");

        Trainer trainer = trainerService.createTrainer("Jane", "Smith", cardio);

        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals("Smith", trainer.getUser().getLastName());
        assertEquals("jane.smith", trainer.getUser().getUsername());
        assertEquals("password123", trainer.getUser().getPassword());
        assertEquals(cardio, trainer.getSpecialization());

        verify(trainerRepository).save(trainer);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void updateTrainer_shouldUpdateSuccessfully() {
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));
        TrainingType yoga = new TrainingType();
        yoga.setTrainingTypeName("YOGA");

        trainerService.updateTrainer("auth.user", "authPass", 1, "Updated", "Name", yoga);

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals("Updated", trainer.getUser().getFirstName());
        assertEquals("Name", trainer.getUser().getLastName());
        assertEquals(yoga, trainer.getSpecialization());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void updateTrainer_shouldThrowException_whenNotFound() {
        when(trainerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer("auth.user", "authPass", 99, "X", "Y", new TrainingType()));
    }

    @Test
    void selectTrainer_shouldReturnTrainer_whenFound() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainer("auth.user", "authPass", 1);

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(trainer, result);
    }

    @Test
    void selectTrainer_shouldThrowException_whenNotFound() {
        when(trainerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainer("auth.user", "authPass", 99));
    }

    @Test
    void findUnassignedTrainers_shouldReturnList() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findTrainerNotInTraineeTrainers("john.doe"))
                .thenReturn(List.of(trainer));

        List<Trainer> result = trainerService.findUnassignedTrainers("auth.user", "authPass", "john.doe");

        verify(authenticationService).authenticate("auth.user", "authPass");
        assertEquals(1, result.size());
        assertEquals(trainer, result.get(0));
    }

    @Test
    void findUnassignedTrainers_shouldReturnEmptyList_whenNoneUnassigned() {
        when(trainerRepository.findTrainerNotInTraineeTrainers("john.doe"))
                .thenReturn(Collections.emptyList());

        List<Trainer> result = trainerService.findUnassignedTrainers("auth.user", "authPass", "john.doe");

        assertTrue(result.isEmpty());
    }

    @Test
    void changePassword_shouldChangeSuccessfully() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("jane.smith");
        user.setPassword("oldPass");
        trainer.setUser(user);
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(trainer));

        trainerService.changePassword("jane.smith", "oldPass", "newPass");

        verify(authenticationService).authenticate("jane.smith", "oldPass");
        assertEquals("newPass", trainer.getUser().getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void toggleActiveStatus_shouldDeactivate_whenCurrentlyActive() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setActive(true);
        trainer.setUser(user);
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));

        trainerService.toggleActiveStatus("auth.user", "authPass", 1);

        assertFalse(trainer.getUser().isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void toggleActiveStatus_shouldActivate_whenCurrentlyInactive() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setActive(false);
        trainer.setUser(user);
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));

        trainerService.toggleActiveStatus("auth.user", "authPass", 1);

        assertTrue(trainer.getUser().isActive());
        verify(trainerRepository).save(trainer);
    }
}