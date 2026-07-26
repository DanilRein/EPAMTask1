package com.example.EPAMtask1.services;

import com.example.EPAMtask1.dao.TraineeDao;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.util.UserCredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private UserCredentialsGenerator credentialsGenerator;
    @InjectMocks
    private TraineeService traineeService;

    @Test
    void createTrainee_ShouldCreateTraineeSuccessfully() {
        when(traineeDao.generateNextId()).thenReturn(1);
        when(credentialsGenerator.generatePassword()).thenReturn("password123");
        when(credentialsGenerator.generateUsername("Test", "Testovich")).thenReturn("test.testovich");

        Trainee trainee = traineeService.createTrainee("Test", "Testovich", LocalDate.of(1990, 1, 1), "123 Test St");

        assertEquals("Test", trainee.getFirstName());
        assertEquals("Testovich", trainee.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Test St", trainee.getAddress());
        assertEquals("test.testovich", trainee.getUsername());
        assertEquals("password123", trainee.getPassword());

        verify(traineeDao).createTrainee(trainee);
    }

    @Test
    void selectTrainee_shouldReturnTrainee_whenFound() {
        Trainee trainee = new Trainee();
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTrainee(1);

        assertEquals(trainee, result);
    }
    @Test
    void selectTrainee_shouldThrowException_whenNotFound() {

        when(traineeDao.selectTrainee(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> traineeService.selectTrainee(99));
    }
    @Test
    void updateTrainee_shouldUpdateTraineeSuccessfully() {
        Trainee trainee = new Trainee();
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.of(trainee));

        traineeService.updateTrainee(1, "Updated", "Name", LocalDate.of(1991, 2, 2), "456 Updated St");

        assertEquals("Updated", trainee.getFirstName());
        assertEquals("Name", trainee.getLastName());
        assertEquals(LocalDate.of(1991, 2, 2), trainee.getDateOfBirth());
        assertEquals("456 Updated St", trainee.getAddress());

        verify(traineeDao).updateTrainee(trainee);
    }
    @Test
    void deleteTrainee_shouldDeleteTraineeSuccessfully() {
        Trainee trainee = new Trainee();
        when(traineeDao.selectTrainee(1)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(1);

        verify(traineeDao).deleteTrainee(1);
    }
}
