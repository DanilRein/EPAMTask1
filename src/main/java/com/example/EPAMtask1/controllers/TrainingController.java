package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.AddTrainingRequest;
import com.example.EPAMtask1.dto.response.TraineeTrainingsResponse;
import com.example.EPAMtask1.dto.response.TrainerTrainingsResponse;
import com.example.EPAMtask1.dto.response.TrainingTypesResponse;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Trainings", description = "Training query and creation endpoints")
public class TrainingController {
    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingController(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        this.gymFacade = gymFacade;
        this.trainingTypeRepository = trainingTypeRepository;
    }


    @Transactional
    @GetMapping("/trainee")
    @Operation(summary = "Get trainee trainings by criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TraineeTrainingsResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TraineeTrainingsResponse>> getTraineeTrainings(@RequestParam String username,
                                                                              @RequestParam(required = false) LocalDate fromDate,
                                                                              @RequestParam(required = false) LocalDate toDate,
                                                                              @RequestParam(required = false) String trainerName,
                                                                              @RequestParam(required = false) String trainingType) {
        List<TraineeTrainingsResponse> trainingsList = gymFacade.getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType).stream()
                .map(training -> new TraineeTrainingsResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName()
                ))
                .toList();
        return ResponseEntity.ok(trainingsList);
    }

    @Transactional
    @GetMapping("/trainer")
    @Operation(summary = "Get trainer trainings by criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerTrainingsResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TrainerTrainingsResponse>> getTrainerTrainings(@RequestParam String username,
                                                                              @RequestParam(required = false) LocalDate fromDate,
                                                                              @RequestParam(required = false) LocalDate toDate,
                                                                              @RequestParam(required = false) String traineeName) {
        List<TrainerTrainingsResponse> trainingsList = gymFacade.getTrainerTrainingsByCriteria(username, fromDate, toDate, traineeName).stream()
                .map(training -> new TrainerTrainingsResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName()
                ))
                .toList();
        return ResponseEntity.ok(trainingsList);
    }

    @GetMapping("/training-types")
    @Operation(summary = "Get available training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingTypesResponse.class))))
    })
    public ResponseEntity<List<TrainingTypesResponse>> getTrainingTypes() {
        List<TrainingTypesResponse> trainingTypes = trainingTypeRepository.findAll().stream()
                .map(trainingType -> new TrainingTypesResponse(trainingType.getTrainingTypeName(), trainingType.getId()))
                .toList();
        return ResponseEntity.ok(trainingTypes);
    }

    @PostMapping
    @Operation(summary = "Create training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer or trainee not found")
    })
    public ResponseEntity<Void> createTraining(@RequestBody @Valid AddTrainingRequest request) {
        gymFacade.createTrainingByUsernames(request.getTraineeUsername(), request.getTrainerUsername(),
                request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration());
        return ResponseEntity.ok().build();
    }


}
