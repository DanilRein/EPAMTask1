package com.example.EPAMtask1.health;

import com.example.EPAMtask1.repository.TrainingTypeRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeHealthIndicator(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public Health health() {
        long count = trainingTypeRepository.count();
        if (count == 0) {
            return Health.down().withDetail("reason", "No training types configured").build();
        }
        return Health.up().withDetail("trainingTypesCount", count).build();
    }
}