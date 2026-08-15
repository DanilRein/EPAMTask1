package com.example.EPAMtask1.repository;

import com.example.EPAMtask1.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByUser_Username(String username);
    @Query("SELECT COUNT(t) FROM Trainer t WHERE t.user.firstName = :firstName AND t.user.lastName = :lastName")
    long countByUser_FirstNameAndUser_LastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    @Query("SELECT t FROM Trainer t WHERE t NOT IN (SELECT tr FROM Trainee tn JOIN tn.trainers tr WHERE tn.user.username = :username)")
    List<Trainer> findTrainerNotInTraineeTrainers(@Param("username") String username);
    boolean existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase(String firstName, String lastName);
}
