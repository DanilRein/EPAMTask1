package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangePasswordRequest;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final GymFacade gymFacade;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public AuthController(AuthenticationService authenticationService, GymFacade gymFacade, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.authenticationService = authenticationService;
        this.gymFacade = gymFacade;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        authenticationService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        if (traineeRepository.findByUser_Username(request.getUsername()).isPresent()) {
            gymFacade.changeTraineePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } else if (trainerRepository.findByUser_Username(request.getUsername()).isPresent()) {
            gymFacade.changeTrainerPassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
