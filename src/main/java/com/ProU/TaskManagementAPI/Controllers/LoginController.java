package com.ProU.TaskManagementAPI.Controllers;

import com.ProU.TaskManagementAPI.DTO.AuthRequest;
import com.ProU.TaskManagementAPI.DTO.AuthResponse;
import com.ProU.TaskManagementAPI.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse resp = authService.authenticate(request);
        if (resp == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody AuthRequest request) {
        AuthResponse resp = authService.authenticate(request);
        if (resp == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String role = resp.role();
        String norm = role == null ? "" : role.toUpperCase();
        if (norm.startsWith("ROLE_")) norm = norm.substring(5);
        if (!"ADMIN".equals(norm)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(resp);
    }
}
