package com.example.EPAMtask1.config;

import com.example.EPAMtask1.model.Trainee;
import com.example.EPAMtask1.model.Trainer;
import com.example.EPAMtask1.model.Training;
import com.example.EPAMtask1.storage.TraineeStorage;
import com.example.EPAMtask1.storage.TrainerStorage;
import com.example.EPAMtask1.storage.TrainingStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StorageInitializer implements BeanPostProcessor {
    @Value("${storage.trainee.file.path}")
    private String traineeFilePath;
    @Value("${storage.trainer.file.path}")
    private String trainerFilePath;
    @Value("${storage.training.file.path}")
    private String trainingFilePath;
    @Autowired
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof TrainingStorage trainingStorage) {
            try {
            ClassPathResource resource = new ClassPathResource(trainingFilePath);
            InputStream inputStream = resource.getInputStream();
            Training[] trainings = objectMapper.readValue(inputStream, Training[].class);
            for (Training training : trainings) {
                trainingStorage.getStorage().put(training.getTrainingId(), training);
            }
        } catch (IOException e) {
                logger.error("Failed to load training data from file: {}", trainingFilePath, e);
            }
        }
        if(bean instanceof TraineeStorage traineeStorage) {
            try {
                ClassPathResource resource = new ClassPathResource(traineeFilePath);
                InputStream inputStream = resource.getInputStream();
                Trainee[] trainees = objectMapper.readValue(inputStream, Trainee[].class);
                for (Trainee trainee : trainees) {
                    traineeStorage.getStorage().put(trainee.getUserId(), trainee);
                }
            } catch (IOException e) {
                logger.error("Failed to load trainee data from file: {}", traineeFilePath, e);
            }
        }
        if(bean instanceof TrainerStorage trainerStorage) {
            try {
                ClassPathResource resource = new ClassPathResource(trainerFilePath);
                InputStream inputStream = resource.getInputStream();
                Trainer[] trainers = objectMapper.readValue(inputStream, Trainer[].class);
                for (Trainer trainer : trainers) {
                    trainerStorage.getStorage().put(trainer.getUserId(), trainer);
                }
            } catch (IOException e) {
                logger.error("Failed to load trainer data from file: {}", trainerFilePath, e);
            }
        }
        return bean;
    }
}
