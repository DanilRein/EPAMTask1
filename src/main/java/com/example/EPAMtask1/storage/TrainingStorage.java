package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Training;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Component
public class TrainingStorage {
    private final Map<Integer, Training> storage = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public int nextId() {
        return idCounter.incrementAndGet();
    }

}
