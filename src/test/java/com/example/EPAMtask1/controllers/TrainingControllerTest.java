package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.AddTrainingRequest;
import com.example.EPAMtask1.exception.GeneralExceptionHandler;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.*;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private GymFacade gymFacade;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private void setUp() {
        TrainingController controller = new TrainingController(gymFacade, trainingTypeRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GeneralExceptionHandler())
                .build();
    }

    private Training buildTraining(String traineeFirst, String traineeLast, String trainerFirst, String trainerLast) {
        User traineeUser = new User();
        traineeUser.setFirstName(traineeFirst);
        traineeUser.setLastName(traineeLast);
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName(trainerFirst);
        trainerUser.setLastName(trainerLast);
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2024, 6, 15));
        training.setTrainingDuration(60);
        return training;
    }

    @Test
    void getTraineeTrainings_shouldReturnList() throws Exception {
        setUp();
        Training training = buildTraining("John", "Doe", "Jane", "Smith");
        when(gymFacade.getTraineeTrainingsByCriteria(eq("john.doe"), eq("pass"), eq("john.doe"),
                isNull(), isNull(), isNull(), isNull())).thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainings/trainee")
                        .param("username", "john.doe")
                        .param("authUsername", "john.doe")
                        .param("authPassword", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Cardio"))
                .andExpect(jsonPath("$[0].trainingType").value("CARDIO"))
                .andExpect(jsonPath("$[0].trainerFullName").value("Jane Smith"));
    }

    @Test
    void getTraineeTrainings_shouldPassOptionalFilters() throws Exception {
        setUp();
        Training training = buildTraining("John", "Doe", "Jane", "Smith");
        when(gymFacade.getTraineeTrainingsByCriteria(eq("john.doe"), eq("pass"), eq("john.doe"),
                eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)), eq("Jane"), eq("CARDIO")))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainings/trainee")
                        .param("username", "john.doe")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .param("trainerName", "Jane")
                        .param("trainingType", "CARDIO")
                        .param("authUsername", "john.doe")
                        .param("authPassword", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTrainerTrainings_shouldReturnList() throws Exception {
        setUp();
        Training training = buildTraining("John", "Doe", "Jane", "Smith");
        when(gymFacade.getTrainerTrainingsByCriteria(eq("jane.smith"), eq("pass"), eq("jane.smith"),
                isNull(), isNull(), isNull())).thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainings/trainer")
                        .param("username", "jane.smith")
                        .param("authUsername", "jane.smith")
                        .param("authPassword", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].traineeFullName").value("John Doe"));
    }

    @Test
    void getTrainingTypes_shouldReturnList() throws Exception {
        setUp();
        TrainingType type = new TrainingType();
        type.setId(1);
        type.setTrainingTypeName("CARDIO");
        when(trainingTypeRepository.findAll()).thenReturn(List.of(type));

        mockMvc.perform(get("/api/trainings/trainingTypes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingTypeName").value("CARDIO"))
                .andExpect(jsonPath("$[0].trainingTypeId").value(1));
    }

    @Test
    void createTraining_shouldReturn200() throws Exception {
        setUp();
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("jane.smith");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2024, 6, 15));
        request.setTrainingDuration(60);

        mockMvc.perform(post("/api/trainings")
                        .param("authUsername", "john.doe")
                        .param("authPassword", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).createTrainingByUsernames("john.doe", "pass", "john.doe", "jane.smith",
                "Morning Cardio", LocalDate.of(2024, 6, 15), 60);
    }

    @Test
    void createTraining_shouldReturn400_whenTrainingNameMissing() throws Exception {
        setUp();
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("jane.smith");
        request.setTrainingDate(LocalDate.of(2024, 6, 15));
        request.setTrainingDuration(60);

        mockMvc.perform(post("/api/trainings")
                        .param("authUsername", "john.doe")
                        .param("authPassword", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
