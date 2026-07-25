package com.example.EPAMtask1.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Trainer extends User{
    private TrainingType specialization;
    private int userId;
}
