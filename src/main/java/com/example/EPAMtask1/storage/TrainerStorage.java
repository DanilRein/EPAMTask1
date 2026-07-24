package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerStorage {
    private final Map<Integer, Trainer> storage = new HashMap<>();

    public Map<Integer, Trainer> getStorage() {
        return storage;
    }
}
