package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Training;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class TrainingStorage {
    private final Map<Integer, Training> storage = new HashMap<>();

}
