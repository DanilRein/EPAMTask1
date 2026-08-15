package com.example.EPAMtask1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypesResponse {
    private String trainingTypeName;
    private Integer trainingTypeId;
}
