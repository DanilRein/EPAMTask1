package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangeActiveStatusRequest;
import com.example.EPAMtask1.dto.request.RegistrationTrainerRequest;
import com.example.EPAMtask1.dto.request.UpdateTrainerRequest;
import com.example.EPAMtask1.dto.response.*;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import com.example.EPAMtask1.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/trainers")
@Tag(name = "Trainers", description = "Trainer profile and lookup endpoints")
public class TrainerController {

    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final JwtService jwtService;

    @Transactional
    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile found",
                    content = @Content(schema = @Schema(implementation = TrainerProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })

    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable String username) {
        TrainerProfileResponse response = findFullTrainerProfile(username);
        return ResponseEntity.ok(response);
    }

    @Transactional
    @GetMapping("/not-assigned/{username}")
    @Operation(summary = "Get active trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unassigned trainers returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerShortInfo.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<List<TrainerShortInfo>> getNotAssignedActiveTrainers(@PathVariable String username) {
        List<Trainer> unassignedTrainers = gymFacade.findUnassignedTrainers(username);
        List<TrainerShortInfo> activeUnassignedTrainers = unassignedTrainers.stream()
                .filter(trainer -> trainer.getUser().isActive())
                .map(trainer -> new TrainerShortInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName()))
                .toList();
        return ResponseEntity.ok(activeUnassignedTrainers);
    }

    @PostMapping
    @Operation(summary = "Register trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created",
                    content = @Content(schema = @Schema(implementation = CredentialsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or duplicate trainee/trainer")
    })
    public ResponseEntity<CredentialsResponse> registrationTrainer(@RequestBody @Valid RegistrationTrainerRequest request) {
        if(traineeRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase(request.getFirstName(), request.getLastName())){
            throw new IllegalArgumentException("A trainee with the same first name and last name already exists.");
        }
        TrainingType specialization = trainingTypeRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization ID"));
        Trainer trainer = gymFacade.createTrainer(request.getFirstName(), request.getLastName(), specialization);
        String token = jwtService.generateToken(trainer.getUser().getUsername());
        CredentialsResponse response = new CredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword(), token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{username}")
    @Transactional
    @Operation(summary = "Update trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated",
                    content = @Content(schema = @Schema(implementation = TrainerProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<TrainerProfileResponse> updateTrainer(@RequestBody @Valid UpdateTrainerRequest request,
                                                                @PathVariable String username) {
        Trainer trainer = trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
        gymFacade.updateTrainerByUsername(username, request.getFirstName(),
                request.getLastName(), trainer.getSpecialization(), request.getIsActive());
        TrainerProfileResponse response = findFullTrainerProfile(username);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    @Operation(summary = "Change trainer active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active status updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<Void> updateTrainerActiveStatus(@RequestBody @Valid ChangeActiveStatusRequest request) {
        gymFacade.setTrainerActiveStatus(request.getUsername(), request.getIsActive());
        return ResponseEntity.ok().build();
    }

    private TrainerProfileResponse findFullTrainerProfile(String username) {
        Trainer trainer = trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
        List<TraineeShortInfo> trainees = trainer.getTrainees().stream()
                .map(trainee -> new TraineeShortInfo(trainee.getUser().getUsername(), trainee.getUser().getFirstName(), trainee.getUser().getLastName()))
                .toList();
        return new TrainerProfileResponse(trainer.getUser().getUsername(), trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName(),
                trainer.getUser().isActive(),trainees);
    }
}
