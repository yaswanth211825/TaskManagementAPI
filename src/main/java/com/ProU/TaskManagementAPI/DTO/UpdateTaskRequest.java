package com.ProU.TaskManagementAPI.DTO;

import java.time.Instant;

// Update may allow nullable fields; validation handled in service/controller
public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        Long assignedToId,
        Instant dueDate
) {
}

