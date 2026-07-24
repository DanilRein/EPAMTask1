package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {
    private final Map<Integer, Trainee> storage = new HashMap<>();

    public Map<Integer, Trainee> getStorage() {
        return storage;
    }
}
