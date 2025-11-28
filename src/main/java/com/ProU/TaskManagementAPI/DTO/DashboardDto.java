package com.ProU.TaskManagementAPI.DTO;

import java.util.List;

public record DashboardDto(
        List<TaskDto> notStarted,
        List<TaskDto> inProgress,
        List<TaskDto> completed,
        List<TaskDto> delayed
) {
}

