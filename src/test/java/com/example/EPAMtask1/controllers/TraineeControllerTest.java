package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangeActiveStatusRequest;
import com.example.EPAMtask1.dto.request.RegistrationTraineeRequest;
import com.example.EPAMtask1.dto.request.UpdateTraineeRequest;
import com.example.EPAMtask1.dto.request.UpdateTraineeTrainersRequest;
import com.example.EPAMtask1.exception.GeneralExceptionHandler;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private GymFacade gymFacade;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private JwtService jwtService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private void setUp() {
        TraineeController controller = new TraineeController(gymFacade, traineeRepository, trainerRepository, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GeneralExceptionHandler())
                .build();
    }

    private Trainee buildTrainee(String username, String firstName, String lastName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("hashedPassword123");
        user.setActive(true);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("Wroclaw");
        trainee.setTrainers(new ArrayList<>());
        return trainee;
    }

    private Trainer buildTrainer(String username, String firstName, String lastName, String specName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(specName);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }

    @Test
    void registerTrainee_shouldReturn201_whenValidRequest() throws Exception {
        setUp();
        RegistrationTraineeRequest request = new RegistrationTraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("Wroclaw");

        when(trainerRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase("John", "Doe"))
                .thenReturn(false);

        Trainee trainee = buildTrainee("john.doe", "John", "Doe");
        when(gymFacade.createTrainee("John", "Doe", LocalDate.of(1990, 1, 1), "Wroclaw"))
                .thenReturn(trainee);
        when(jwtService.generateToken("john.doe")).thenReturn("jwt-token-123");

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("hashedPassword123"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void registerTrainee_shouldReturn400_whenFirstNameBlank() throws Exception {
        setUp();
        RegistrationTraineeRequest request = new RegistrationTraineeRequest();
        request.setFirstName("");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerTrainee_shouldReturn404_whenTrainerWithSameNameExists() throws Exception {
        setUp();
        RegistrationTraineeRequest request = new RegistrationTraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        when(trainerRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase("John", "Doe"))
                .thenReturn(true);

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTraineeProfile_shouldReturnProfile_whenFound() throws Exception {
        setUp();
        Trainee trainee = buildTrainee("john.doe", "John", "Doe");
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        mockMvc.perform(get("/api/trainees/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getTraineeProfile_shouldReturn404_whenNotFound() throws Exception {
        setUp();
        when(traineeRepository.findByUser_Username("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainees/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTrainee_shouldReturnUpdatedProfile() throws Exception {
        setUp();
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("john.doe");
        request.setFirstName("Johnny");
        request.setLastName("Doe");
        request.setIsActive(true);

        Trainee trainee = buildTrainee("john.doe", "Johnny", "Doe");
        when(traineeRepository.findByUser_Username("john.doe")).thenReturn(Optional.of(trainee));

        mockMvc.perform(put("/api/trainees/john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"));

        verify(gymFacade).updateTraineeByUsername("john.doe",
                "Johnny", "Doe", null, null, true);
    }

    @Test
    void deleteTrainee_shouldReturn200() throws Exception {
        setUp();
        mockMvc.perform(delete("/api/trainees"))
                .andExpect(status().isOk());

        verify(gymFacade).deleteTrainee();
    }

    @Test
    void updateTraineeTrainers_shouldReturnUpdatedList() throws Exception {
        setUp();
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsernames(List.of("jane.smith"));

        Trainer trainer = buildTrainer("jane.smith", "Jane", "Smith", "CARDIO");
        when(gymFacade.updateTraineeTrainersByUsername("john.doe", List.of("jane.smith")))
                .thenReturn(List.of(trainer));

        mockMvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"))
                .andExpect(jsonPath("$[0].specialization").value("CARDIO"));
    }

    @Test
    void updateTraineeActiveStatus_shouldReturn200() throws Exception {
        setUp();
        ChangeActiveStatusRequest request = new ChangeActiveStatusRequest();
        request.setUsername("john.doe");
        request.setIsActive(false);

        mockMvc.perform(patch("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).setTraineeActiveStatus("john.doe", false);
    }
}
