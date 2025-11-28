package com.ProU.TaskManagementAPI.Services;

import com.ProU.TaskManagementAPI.DTO.*;

import java.util.List;

public interface TaskService {
    TaskDto create(CreateTaskRequest req);
    TaskDto update(Long id, UpdateTaskRequest req);
    void delete(Long id);
    TaskDto getById(Long id);
    List<TaskDto> listAll();
    DashboardDto dashboard();
}

