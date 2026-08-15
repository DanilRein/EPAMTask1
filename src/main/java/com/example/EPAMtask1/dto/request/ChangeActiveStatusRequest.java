package com.example.EPAMtask1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeActiveStatusRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotNull(message = "Active status cannot be null")
    private Boolean isActive;
}
