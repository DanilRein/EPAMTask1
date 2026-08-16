package com.example.EPAMtask1.health;

import com.example.EPAMtask1.repository.TrainerRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrainersHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    public ActiveTrainersHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        long activeCount = trainerRepository.findAll().stream()
                .filter(trainer -> trainer.getUser().isActive())
                .count();
        if (activeCount == 0) {
            return Health.down().withDetail("reason", "No active trainers available").build();
        }
        return Health.up().withDetail("activeTrainersCount", activeCount).build();
    }
}