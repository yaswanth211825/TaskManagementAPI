package com.ProU.TaskManagementAPI.DTO;

import jakarta.validation.constraints.NotBlank;

public record ApproveUserRequest(
        @NotBlank String password,
        @NotBlank String role
) {}
