package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.RegistrationTraineeRequest;
import com.example.EPAMtask1.dto.request.UpdateTraineeRequest;
import com.example.EPAMtask1.dto.response.CredentialsResponse;
import com.example.EPAMtask1.dto.response.TraineeProfileResponse;
import com.example.EPAMtask1.dto.response.TrainerShortInfo;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;

    public TraineeController(GymFacade gymFacade, TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.traineeRepository = traineeRepository;
        this.gymFacade = gymFacade;
    }
    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> registerTrainee(@RequestBody @Valid RegistrationTraineeRequest request) {
        Trainee trainee = gymFacade.createTrainee(request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getAddress());
        CredentialsResponse response = new CredentialsResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Transactional
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable String username) {
        TraineeProfileResponse profile = findFullTraineeProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    @Transactional
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
    public ResponseEntity<Void> deleteTrainee(@RequestParam String authUsername,
                                              @RequestParam String authPassword) {
        gymFacade.deleteTrainee(authUsername, authPassword);
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
