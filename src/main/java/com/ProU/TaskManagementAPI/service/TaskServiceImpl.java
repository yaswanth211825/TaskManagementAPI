package com.ProU.TaskManagementAPI.service;

import com.ProU.TaskManagementAPI.DTO.*;
import com.ProU.TaskManagementAPI.Services.TaskService;
import com.ProU.TaskManagementAPI.entity.Task;
import com.ProU.TaskManagementAPI.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private TaskDto toDto(Task t) {
        return new TaskDto(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getAssignedToId(), t.getDueDate(), t.getCreatedAt(), t.getUpdatedAt());
    }

    @Override
    public TaskDto create(CreateTaskRequest req) {
        Task t = new Task(req.title(), req.description(), req.status(), req.assignedToId(), req.dueDate());
        Task saved = taskRepository.save(t);
        return toDto(saved);
    }

    @Override
    public TaskDto update(Long id, UpdateTaskRequest req) {
        Task existing = taskRepository.findById(id).orElse(null);
        if (existing == null) return null;
        if (req.title() != null) existing.setTitle(req.title());
        if (req.description() != null) existing.setDescription(req.description());
        if (req.status() != null) existing.setStatus(req.status());
        if (req.assignedToId() != null) existing.setAssignedToId(req.assignedToId());
        if (req.dueDate() != null) existing.setDueDate(req.dueDate());
        existing.setUpdatedAt(Instant.now());
        Task saved = taskRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto getById(Long id) {
        return taskRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<TaskDto> listAll() {
        return taskRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
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

