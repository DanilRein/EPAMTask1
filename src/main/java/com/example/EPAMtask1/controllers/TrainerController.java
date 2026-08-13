package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.RegistrationTrainerRequest;
import com.example.EPAMtask1.dto.response.CredentialsResponse;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.TrainingType;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainerController(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        this.gymFacade = gymFacade;
        this.trainingTypeRepository = trainingTypeRepository;
    }
    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> registrationTrainer(@RequestBody @Valid RegistrationTrainerRequest request) {
        TrainingType specialization = trainingTypeRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization ID"));
        Trainer trainer = gymFacade.createTrainer(request.getFirstName(), request.getLastName(), specialization);
        CredentialsResponse response = new CredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
