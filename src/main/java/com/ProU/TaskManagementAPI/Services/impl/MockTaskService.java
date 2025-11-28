package com.ProU.TaskManagementAPI.Services.impl;

import com.ProU.TaskManagementAPI.DTO.*;
import com.ProU.TaskManagementAPI.Services.TaskService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MockTaskService implements TaskService {

    private final Map<Long, TaskDto> store = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public TaskDto create(CreateTaskRequest req) {
        Long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        TaskDto t = new TaskDto(id, req.title(), req.description(), req.status(), req.assignedToId(), req.dueDate(), now, now);
        store.put(id, t);
        return t;
    }

    @Override
    public TaskDto update(Long id, UpdateTaskRequest req) {
        TaskDto existing = store.get(id);
        if (existing == null) return null;
        TaskDto updated = new TaskDto(
                id,
                req.title() != null ? req.title() : existing.title(),
                req.description() != null ? req.description() : existing.description(),
                req.status() != null ? req.status() : existing.status(),
                req.assignedToId() != null ? req.assignedToId() : existing.assignedToId(),
                req.dueDate() != null ? req.dueDate() : existing.dueDate(),
                existing.createdAt(),
                Instant.now()
        );
        store.put(id, updated);
        return updated;
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    @Override
    public TaskDto getById(Long id) {
        return store.get(id);
    }

    @Override
    public List<TaskDto> listAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public DashboardDto dashboard() {
        List<TaskDto> all = listAll();
        List<TaskDto> notStarted = all.stream().filter(t -> t.status() == TaskStatus.NOT_STARTED).collect(Collectors.toList());
        List<TaskDto> inProgress = all.stream().filter(t -> t.status() == TaskStatus.IN_PROGRESS).collect(Collectors.toList());
        List<TaskDto> completed = all.stream().filter(t -> t.status() == TaskStatus.COMPLETED).collect(Collectors.toList());
        List<TaskDto> delayed = all.stream().filter(t -> t.dueDate() != null && t.dueDate().isBefore(Instant.now()) && t.status() != TaskStatus.COMPLETED).collect(Collectors.toList());
        return new DashboardDto(notStarted, inProgress, completed, delayed);
    }
}
