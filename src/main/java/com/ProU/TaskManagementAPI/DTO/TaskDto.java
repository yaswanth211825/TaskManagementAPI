package com.ProU.TaskManagementAPI.DTO;

import java.time.Instant;

public record TaskDto(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Long assignedToId,
        Instant dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}

