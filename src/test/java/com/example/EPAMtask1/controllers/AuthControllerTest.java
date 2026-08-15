package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangePasswordRequest;
import com.example.EPAMtask1.exception.AuthenticationException;
import com.example.EPAMtask1.exception.GeneralExceptionHandler;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private GymFacade gymFacade;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void setUp() {
        AuthController controller = new AuthController(authenticationService, gymFacade, traineeRepository, trainerRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GeneralExceptionHandler())
                .build();
    }

    @Test
    void login_shouldReturn200_whenValidCredentials() throws Exception {
        setUp();
        doNothing().when(authenticationService).authenticate("john.doe", "pass123");

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials() throws Exception {
        setUp();
        doThrow(new AuthenticationException("Username or password is invalid"))
                .when(authenticationService).authenticate("john.doe", "wrong");

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_shouldChangeForTrainee() throws Exception {
        setUp();
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("john.doe");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(new Trainee()));

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).changeTraineePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void changePassword_shouldChangeForTrainer_whenNotTrainee() throws Exception {
        setUp();
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("jane.smith");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(traineeRepository.findByUser_Username("jane.smith")).thenReturn(Optional.empty());
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(new com.example.EPAMtask1.model.Trainer()));

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).changeTrainerPassword("jane.smith", "oldPass", "newPass");
    }

    @Test
    void changePassword_shouldReturn400_whenUserNotFoundAsTraineeOrTrainer() throws Exception {
        setUp();
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("unknown");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(traineeRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());
        when(trainerRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}