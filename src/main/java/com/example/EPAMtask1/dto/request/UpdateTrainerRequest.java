package com.example.EPAMtask1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTrainerRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;
}
