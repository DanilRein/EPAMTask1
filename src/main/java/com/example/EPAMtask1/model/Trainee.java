package com.example.EPAMtask1.model;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
    private int userId;
}
