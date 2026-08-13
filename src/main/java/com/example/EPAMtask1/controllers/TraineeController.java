package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.RegistrationTraineeRequest;
import com.example.EPAMtask1.dto.response.CredentialsResponse;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
    private final GymFacade gymFacade;

    public TraineeController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }
    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> registerTrainee(@RequestBody @Valid RegistrationTraineeRequest request) {
        Trainee trainee = gymFacade.createTrainee(request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getAddress());
        CredentialsResponse response = new CredentialsResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
