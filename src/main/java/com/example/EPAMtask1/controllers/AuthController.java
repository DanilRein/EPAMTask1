package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.ChangePasswordRequest;
import com.example.EPAMtask1.dto.request.LoginRequest;
import com.example.EPAMtask1.dto.response.TokenResponse;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.repository.TraineeRepository;
import com.example.EPAMtask1.repository.TrainerRepository;
import com.example.EPAMtask1.services.AuthenticationService;
import com.example.EPAMtask1.services.JwtService;
import com.example.EPAMtask1.services.TokenBlacklistService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and credentials management")
@AllArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;
    private final GymFacade gymFacade;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        authenticationService.authenticate(request.getUsername(), request.getPassword());
        logger.info("User authenticated successfully: {}", request.getUsername());
        TokenResponse tokenResponse = new TokenResponse(jwtService.generateToken(request.getUsername()));
        return ResponseEntity.ok(tokenResponse);
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
        logger.info("Password change requested for user: {}", request.getUsername());
        if (traineeRepository.findByUser_Username(request.getUsername()).isPresent()) {
            gymFacade.changeTraineePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } else if (trainerRepository.findByUser_Username(request.getUsername()).isPresent()) {
            gymFacade.changeTrainerPassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        tokenBlacklistService.blacklist(token);
        logger.info("User logged out");
        return ResponseEntity.ok().build();
    }
}
