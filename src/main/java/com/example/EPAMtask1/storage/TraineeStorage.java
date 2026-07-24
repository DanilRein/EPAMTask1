package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Trainee;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Component
public class TraineeStorage {
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final Map<Integer, Trainee> storage = new HashMap<>();

    public int nextId() {
        return idCounter.incrementAndGet();
    }

}
