package com.example.EPAMtask1.repository;

import com.example.EPAMtask1.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Integer> {
    @Query("SELECT tr FROM Training tr WHERE tr.trainee.user.username = :username " +
            "AND (CAST(:fromDate AS date) IS NULL OR tr.trainingDate >= :fromDate) " +
            "AND (CAST(:toDate AS date) IS NULL OR tr.trainingDate <= :toDate) " +
            "AND (:trainerName IS NULL OR tr.trainer.user.firstName = :trainerName) " +
            "AND (:trainingType IS NULL OR tr.trainingType.trainingTypeName = :trainingType)")
    List<Training> findByTraineeUsernameAndDateAndTrainerAndType(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType);

    @Query("SELECT tr FROM Training tr WHERE tr.trainer.user.username = :username " +
            "AND (CAST(:fromDate AS date) IS NULL OR tr.trainingDate >= :fromDate) " +
            "AND (CAST(:toDate AS date) IS NULL OR tr.trainingDate <= :toDate) " +
            "AND (:traineeName IS NULL OR tr.trainee.user.firstName = :traineeName)")
    List<Training> findByTrainerUserUsernameAndDateAndTrainee(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName);
}
