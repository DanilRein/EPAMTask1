package com.example.EPAMtask1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Trainee{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String address;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToMany(
            mappedBy = "trainees"
    )
    private List<Trainer> trainers;

}
