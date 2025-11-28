package com.ProU.TaskManagementAPI.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        @NotNull TaskStatus status,
        Long assignedToId,
        Instant dueDate
) {
}

