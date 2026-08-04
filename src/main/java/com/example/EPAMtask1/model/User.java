package com.example.EPAMtask1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @JsonProperty("isActive")
    private boolean isActive = true;

    @OneToOne(mappedBy = "user")
    private Trainee trainee;
    @OneToOne(mappedBy = "user")
    private Trainer trainer;

}
