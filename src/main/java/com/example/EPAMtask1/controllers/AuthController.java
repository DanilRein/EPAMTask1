package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangePasswordRequest;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and credentials management")
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

    @GetMapping
    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        authenticationService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Invalid old password"),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
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
