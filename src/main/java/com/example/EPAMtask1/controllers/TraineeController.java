package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.auth.Authentication;
import com.example.EPAMtask1.dto.request.ChangeActiveStatusRequest;
import com.example.EPAMtask1.dto.request.RegistrationTraineeRequest;
import com.example.EPAMtask1.dto.request.UpdateTraineeRequest;
import com.example.EPAMtask1.dto.request.UpdateTraineeTrainersRequest;
import com.example.EPAMtask1.dto.response.CredentialsResponse;
import com.example.EPAMtask1.dto.response.TraineeProfileResponse;
import com.example.EPAMtask1.dto.response.TrainerShortInfo;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Tag(name = "Trainees", description = "Trainee profile and trainer assignment endpoints")
public class TraineeController {
    private final GymFacade gymFacade;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public TraineeController(GymFacade gymFacade,TrainerRepository trainerRepository, TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.gymFacade = gymFacade;
    }
    @PostMapping
    @Operation(summary = "Register trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee created",
                    content = @Content(schema = @Schema(implementation = CredentialsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or duplicate trainee/trainer")
    })
    public ResponseEntity<CredentialsResponse> registerTrainee(@RequestBody @Valid RegistrationTraineeRequest request) {
        if(trainerRepository.existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase(request.getFirstName(), request.getLastName())){
            throw new IllegalArgumentException("A trainer with the same first name and last name already exists.");
        }
        Trainee trainee = gymFacade.createTrainee(request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getAddress());
        CredentialsResponse response = new CredentialsResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Transactional
    @Operation(summary = "Get trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile found",
                    content = @Content(schema = @Schema(implementation = TraineeProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @Authentication
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@RequestParam String authUsername,
                                                                    @RequestParam String authPassword,
                                                                    @PathVariable String username) {
        TraineeProfileResponse profile = findFullTraineeProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    @Transactional
    @Operation(summary = "Update trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated",
                    content = @Content(schema = @Schema(implementation = TraineeProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<TraineeProfileResponse> updateTrainee(@RequestBody @Valid UpdateTraineeRequest request,
                                              @PathVariable String username,
                                              @RequestParam String authUsername,
                                              @RequestParam String authPassword) {
        gymFacade.updateTraineeByUsername(authUsername, authPassword, username, request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getAddress(), request.getIsActive());
        TraineeProfileResponse profile = findFullTraineeProfile(username);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping
    @Transactional
    @Operation(summary = "Delete trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<Void> deleteTrainee(@RequestParam String authUsername,
                                              @RequestParam String authPassword) {
        gymFacade.deleteTrainee(authUsername, authPassword);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/trainers")
    @Transactional
    @Operation(summary = "Update trainee trainer list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer list updated",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerShortInfo.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    public ResponseEntity<List<TrainerShortInfo>> updateTraineeTrainers(@RequestBody @Valid UpdateTraineeTrainersRequest request,
                                                                        @RequestParam String authUsername,
                                                                        @RequestParam String authPassword) {
        List<TrainerShortInfo> updatedTrainers = gymFacade.updateTraineeTrainersByUsername(authUsername, authPassword,
                request.getTraineeUsername(), request.getTrainerUsernames()).stream()
                .map(trainer -> new TrainerShortInfo(trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName()))
                .toList();
        return ResponseEntity.ok(updatedTrainers);
    }

    @PatchMapping
    @Operation(summary = "Change trainee active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active status updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<Void> updateTraineeActiveStatus(@RequestBody @Valid ChangeActiveStatusRequest request,
                                                          @RequestParam String authUsername,
                                                          @RequestParam String authPassword) {
        gymFacade.setTraineeActiveStatus(authUsername, authPassword, request.getUsername(), request.getIsActive());
        return ResponseEntity.ok().build();
    }

    private TraineeProfileResponse findFullTraineeProfile(String username) {
        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow(() -> new IllegalArgumentException("Trainee not found"));
        List<TrainerShortInfo> trainers = trainee.getTrainers().stream()
                .map(trainer -> new TrainerShortInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName()))
                .toList();
        return new TraineeProfileResponse(trainee.getUser().getUsername(), trainee.getUser().getFirstName(), trainee.getUser().getLastName(),
                trainee.getDateOfBirth(), trainee.getAddress(), trainers, trainee.getUser().isActive());
    }


}
