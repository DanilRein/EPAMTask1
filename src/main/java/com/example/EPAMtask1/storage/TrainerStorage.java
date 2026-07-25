package com.example.EPAMtask1.storage;

import com.example.EPAMtask1.model.Trainer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Component
public class TrainerStorage {
    private final Map<Integer, Trainer> storage = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    public int nextId() {
        return idCounter.incrementAndGet();
    }
    public void ensureIdAtLeast(int id) {
        idCounter.updateAndGet(current -> Math.max(current, id));
    }
}
