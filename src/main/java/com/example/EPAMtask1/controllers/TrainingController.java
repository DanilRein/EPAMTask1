package com.example.EPAMtask1.controllers;

import com.example.EPAMtask1.dto.request.AddTrainingRequest;
import com.example.EPAMtask1.dto.response.TraineeTrainingsResponse;
import com.example.EPAMtask1.dto.response.TrainerTrainingsResponse;
import com.example.EPAMtask1.dto.response.TrainingTypesResponse;
import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {
    private final GymFacade gymFacade;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingController(GymFacade gymFacade, TrainingTypeRepository trainingTypeRepository) {
        this.gymFacade = gymFacade;
        this.trainingTypeRepository = trainingTypeRepository;
    }


    @Transactional
    @GetMapping("/trainee")
    public ResponseEntity<List<TraineeTrainingsResponse>> getTraineeTrainings(@RequestParam String username,
                                                                              @RequestParam(required = false) LocalDate fromDate,
                                                                              @RequestParam(required = false) LocalDate toDate,
                                                                              @RequestParam(required = false) String trainerName,
                                                                              @RequestParam(required = false) String trainingType,
                                                                              @RequestParam String authUsername,
                                                                              @RequestParam String authPassword) {
        List<TraineeTrainingsResponse> trainingsList = gymFacade.getTraineeTrainingsByCriteria(authUsername, authPassword, username, fromDate, toDate, trainerName, trainingType).stream()
                .map(training -> new TraineeTrainingsResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName()
                ))
                .toList();
        return ResponseEntity.ok(trainingsList);
    }

    @Transactional
    @GetMapping("/trainer")
    public ResponseEntity<List<TrainerTrainingsResponse>> getTrainerTrainings(@RequestParam String username,
                                                                              @RequestParam(required = false) LocalDate fromDate,
                                                                              @RequestParam(required = false) LocalDate toDate,
                                                                              @RequestParam(required = false) String traineeName,
                                                                              @RequestParam String authUsername,
                                                                              @RequestParam String authPassword) {
        List<TrainerTrainingsResponse> trainingsList = gymFacade.getTrainerTrainingsByCriteria(authUsername, authPassword, username, fromDate, toDate, traineeName).stream()
                .map(training -> new TrainerTrainingsResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName()
                ))
                .toList();
        return ResponseEntity.ok(trainingsList);
    }

    @GetMapping("/trainingTypes")
    public ResponseEntity<List<TrainingTypesResponse>> getTrainingTypes() {
        List<TrainingTypesResponse> trainingTypes = trainingTypeRepository.findAll().stream()
                .map(trainingType -> new TrainingTypesResponse(trainingType.getTrainingTypeName(), trainingType.getId()))
                .toList();
        return ResponseEntity.ok(trainingTypes);
    }

    @PostMapping
    public ResponseEntity<Void> createTraining(@RequestBody @Valid AddTrainingRequest request,
                                               @RequestParam String authUsername,
                                               @RequestParam String authPassword) {
        gymFacade.createTrainingByUsernames(authUsername, authPassword, request.getTraineeUsername(), request.getTrainerUsername(),
                request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration());
        return ResponseEntity.ok().build();
    }


}
