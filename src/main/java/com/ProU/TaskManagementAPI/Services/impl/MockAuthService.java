package com.ProU.TaskManagementAPI.Services.impl;

import com.ProU.TaskManagementAPI.DTO.AuthRequest;
import com.ProU.TaskManagementAPI.DTO.AuthResponse;
import com.ProU.TaskManagementAPI.Services.AuthService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MockAuthService implements AuthService {

    // simple in-memory user store: username -> (password, role)
    private final Map<String, UserRecord> users = new HashMap<>();

    public MockAuthService() {
        users.put("user1", new UserRecord("pass1", "USER"));
        users.put("admin", new UserRecord("adminpass", "ADMIN"));
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return null;
        }

        UserRecord rec = users.get(request.username());
        if (rec != null && rec.password.equals(request.password())) {
            String payload = request.username() + ":" + rec.role + ":" + Instant.now().toEpochMilli();
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            return new AuthResponse(token, rec.role);
        }

        return null;
    }

    private static class UserRecord {
        final String password;
        final String role;

        UserRecord(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }
}
