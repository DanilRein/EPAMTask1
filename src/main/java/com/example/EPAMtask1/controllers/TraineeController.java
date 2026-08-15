package com.example.EPAMtask1.controllers;

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
import com.example.EPAMtask1.services.AuthenticationService;
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
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final AuthenticationService authenticationService;

    public TraineeController(GymFacade gymFacade,TrainerRepository trainerRepository, TraineeRepository traineeRepository, AuthenticationService authenticationService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.gymFacade = gymFacade;
        this.authenticationService = authenticationService;

    }
    @PostMapping("/register")
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
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable String username,
                                                                    @RequestParam String authUsername,
                                                                    @RequestParam String authPassword) {
        authenticationService.authenticate(authUsername, authPassword);
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
    @PutMapping("/update-trainers")
    @Transactional
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
