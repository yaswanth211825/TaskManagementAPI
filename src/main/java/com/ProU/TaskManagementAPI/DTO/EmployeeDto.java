package com.ProU.TaskManagementAPI.DTO;

import java.time.Instant;

public record EmployeeDto(Long id, String name, String email, Instant createdAt) {
}

