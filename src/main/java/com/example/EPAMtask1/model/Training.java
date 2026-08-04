package com.example.EPAMtask1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "traineeId", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainerId", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "trainingTypeId", nullable = false)
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private int trainingDuration;
}
