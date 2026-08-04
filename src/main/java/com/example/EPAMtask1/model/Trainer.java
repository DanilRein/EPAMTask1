package com.example.EPAMtask1.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "trainingTypeId", nullable = false)
    private TrainingType specialization;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainerId"),
            inverseJoinColumns = @JoinColumn(name = "traineeId")
    )
    private List<Trainee> trainees;
}
