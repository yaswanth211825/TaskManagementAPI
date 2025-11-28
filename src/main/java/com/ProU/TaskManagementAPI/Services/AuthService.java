package com.ProU.TaskManagementAPI.Services;

import com.ProU.TaskManagementAPI.DTO.AuthRequest;
import com.ProU.TaskManagementAPI.DTO.AuthResponse;

public interface AuthService {
    /**
     * Authenticate given request and return AuthResponse with token if successful, or null if not.
     */
    AuthResponse authenticate(AuthRequest request);
}

