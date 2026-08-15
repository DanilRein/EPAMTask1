package com.example.EPAMtask1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddTrainingRequest {
    @NotBlank(message = "Trainer username cannot be blank")
    private String trainerUsername;
    @NotBlank(message = "Trainee username cannot be blank")
    private String traineeUsername;
    @NotBlank(message = "Training type name cannot be blank")
    private String trainingName;
    @NotNull(message = "Training type name cannot be blank")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration cannot be null")
    private Integer trainingDuration;
}
