package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangeActiveStatusRequest;
import com.example.EPAMtask1.dto.request.RegistrationTrainerRequest;
import com.example.EPAMtask1.dto.request.UpdateTrainerRequest;
import com.example.EPAMtask1.exception.AuthenticationException;
import com.example.EPAMtask1.exception.GeneralExceptionHandler;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private GymFacade gymFacade;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TraineeRepository traineeRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void setUp() {
        TrainerController controller = new TrainerController(gymFacade, trainingTypeRepository, trainerRepository, authenticationService, traineeRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GeneralExceptionHandler())
                .build();
    }

    private Trainer buildTrainer(String username, String firstName, String lastName, String specName, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("password123");
        user.setActive(active);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(specName);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        trainer.setTrainees(new ArrayList<>());
        return trainer;
    }

    @Test
    void registerTrainer_shouldReturn201_whenValid() throws Exception {
        setUp();
        RegistrationTrainerRequest request = new RegistrationTrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecializationId(1);

        when(traineeRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase("Jane", "Smith"))
                .thenReturn(false);

        TrainingType type = new TrainingType();
        type.setId(1);
        type.setTrainingTypeName("CARDIO");
        when(trainingTypeRepository.findById(1)).thenReturn(Optional.of(type));

        Trainer trainer = buildTrainer("jane.smith", "Jane", "Smith", "CARDIO", true);
        when(gymFacade.createTrainer("Jane", "Smith", type)).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("jane.smith"));
    }

    @Test
    void registerTrainer_shouldReturn404_whenTraineeWithSameNameExists() throws Exception {
        setUp();
        RegistrationTrainerRequest request = new RegistrationTrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecializationId(1);

        when(traineeRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase("Jane", "Smith"))
                .thenReturn(true);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerTrainer_shouldReturn404_whenSpecializationNotFound() throws Exception {
        setUp();
        RegistrationTrainerRequest request = new RegistrationTrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecializationId(99);

        when(traineeRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase("Jane", "Smith"))
                .thenReturn(false);
        when(trainingTypeRepository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrainerProfile_shouldReturnProfile() throws Exception {
        setUp();
        Trainer trainer = buildTrainer("jane.smith", "Jane", "Smith", "CARDIO", true);
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/api/trainers/jane.smith")
                        .param("authUsername", "jane.smith")
                        .param("authPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.specialization").value("CARDIO"));

        verify(authenticationService).authenticate("jane.smith", "password123");
    }

    @Test
    void getTrainerProfile_shouldReturn401_whenAuthFails() throws Exception {
        setUp();
        doThrow(new AuthenticationException("Username or password is invalid"))
                .when(authenticationService).authenticate("jane.smith", "wrong");

        mockMvc.perform(get("/api/trainers/jane.smith")
                        .param("authUsername", "jane.smith")
                        .param("authPassword", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateTrainer_shouldKeepSpecializationReadOnly() throws Exception {
        setUp();
        Trainer trainer = buildTrainer("jane.smith", "Jane", "Smith", "CARDIO", true);
        when(trainerRepository.findByUser_Username("jane.smith")).thenReturn(Optional.of(trainer));

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("jane.smith");
        request.setFirstName("Janet");
        request.setLastName("Smith");
        request.setIsActive(true);

        mockMvc.perform(put("/api/trainers/jane.smith")
                        .param("authUsername", "jane.smith")
                        .param("authPassword", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("CARDIO"));

        verify(gymFacade).updateTrainerByUsername("jane.smith", "password123", "jane.smith",
                "Janet", "Smith", trainer.getSpecialization(), true);
    }

    @Test
    void getNotAssignedActiveTrainers_shouldReturnOnlyActiveOnes() throws Exception {
        setUp();
        Trainer activeTrainer = buildTrainer("mark.jones", "Mark", "Jones", "YOGA", true);
        Trainer inactiveTrainer = buildTrainer("bob.brown", "Bob", "Brown", "STRENGTH", false);

        when(gymFacade.findUnassignedTrainers("john.doe", "password123", "john.doe"))
                .thenReturn(List.of(activeTrainer, inactiveTrainer));

        mockMvc.perform(get("/api/trainers/not-assigned/john.doe")
                        .param("authUsername", "john.doe")
                        .param("authPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("mark.jones"));
    }

    @Test
    void updateTrainerActiveStatus_shouldReturn200() throws Exception {
        setUp();
        ChangeActiveStatusRequest request = new ChangeActiveStatusRequest();
        request.setUsername("jane.smith");
        request.setIsActive(false);

        mockMvc.perform(patch("/api/trainers")
                        .param("authUsername", "jane.smith")
                        .param("authPassword", "password123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).setTrainerActiveStatus("jane.smith", "password123", "jane.smith", false);
    }
}