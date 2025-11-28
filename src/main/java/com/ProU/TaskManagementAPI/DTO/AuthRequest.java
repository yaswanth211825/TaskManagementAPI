package com.ProU.TaskManagementAPI.DTO;

import jakarta.validation.constraints.NotBlank;

/** Authentication request */
public record AuthRequest(
        @NotBlank(message = "username must not be blank") String username,
        @NotBlank(message = "password must not be blank") String password
) {
}

