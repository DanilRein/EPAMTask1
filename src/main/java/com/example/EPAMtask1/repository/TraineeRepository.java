package com.example.EPAMtask1.repository;

import com.example.EPAMtask1.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    @Query("SELECT COUNT(t) FROM Trainee t WHERE t.user.firstName = :firstName AND t.user.lastName = :lastName")
    long countByUser_FirstNameAndUser_LastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    Optional<Trainee> findByUser_Username(String username);
    boolean existsByUser_FirstNameIgnoreCaseAndUser_LastNameIgnoreCase(String firstName, String lastName);
}
