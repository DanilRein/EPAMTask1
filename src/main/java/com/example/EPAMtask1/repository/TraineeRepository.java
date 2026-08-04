package com.example.EPAMtask1.repository;

import com.example.EPAMtask1.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    Optional<Trainee> findByUser_Username(String username);
}
