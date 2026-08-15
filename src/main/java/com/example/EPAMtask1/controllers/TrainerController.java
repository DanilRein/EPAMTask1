package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.RegistrationTrainerRequest;
import com.example.EPAMtask1.dto.request.UpdateTrainerRequest;
import com.example.EPAMtask1.dto.response.*;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final AuthenticationService authenticationService;

    public TrainerController(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository, TrainerRepository trainerRepository, AuthenticationService authenticationService) {
        this.gymFacade = gymFacade;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainerRepository = trainerRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable String username,
                                                                    @RequestParam String authUsername,
                                                                    @RequestParam String authPassword) {
        authenticationService.authenticate(authUsername, authPassword);
        TrainerProfileResponse response = findFullTrainerProfile(username);
        return ResponseEntity.ok(response);
    }

    @Transactional
    @GetMapping("/not-assigned-active-trainers/{username}")
    public ResponseEntity<List<TrainerShortInfo>> getNotAssignedActiveTrainers(@PathVariable String username,
                                                                               @RequestParam String authUsername,
                                                                               @RequestParam String authPassword) {
        List<Trainer> unassignedTrainers = gymFacade.findUnassignedTrainers(authUsername, authPassword, username);
        List<TrainerShortInfo> activeUnassignedTrainers = unassignedTrainers.stream()
                .filter(trainer -> trainer.getUser().isActive())
                .map(trainer -> new TrainerShortInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName()))
                .toList();
        return ResponseEntity.ok(activeUnassignedTrainers);
    }

    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> registrationTrainer(@RequestBody @Valid RegistrationTrainerRequest request) {
        TrainingType specialization = trainingTypeRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization ID"));
        Trainer trainer = gymFacade.createTrainer(request.getFirstName(), request.getLastName(), specialization);
        CredentialsResponse response = new CredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{username}")
    @Transactional
    public ResponseEntity<TrainerProfileResponse> updateTrainer(@RequestBody @Valid UpdateTrainerRequest request,
                                                                @PathVariable String username,
                                                                @RequestParam String authUsername,
                                                                @RequestParam String authPassword) {
        Trainer trainer = trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));
        gymFacade.updateTrainerByUsername(authUsername, authPassword, username, request.getFirstName(),
                request.getLastName(), trainer.getSpecialization(), request.getIsActive());
        TrainerProfileResponse response = findFullTrainerProfile(username);
        return ResponseEntity.ok(response);
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
