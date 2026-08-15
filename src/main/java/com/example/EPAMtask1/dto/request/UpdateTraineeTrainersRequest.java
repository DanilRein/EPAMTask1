package com.example.EPAMtask1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTraineeTrainersRequest {
    @NotBlank(message = "Trainee username cannot be blank")
    private String traineeUsername;
    @NotEmpty(message = "Trainer usernames cannot be blank")
    private List<String> trainerUsernames;
}
