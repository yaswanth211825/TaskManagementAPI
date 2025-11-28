package com.ProU.TaskManagementAPI.Controllers;

import com.ProU.TaskManagementAPI.DTO.SignupRequest;
import com.ProU.TaskManagementAPI.entity.PendingSignup;
import com.ProU.TaskManagementAPI.repository.PendingSignupRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SignupController {

    private final PendingSignupRepository pendingSignupRepository;

    public SignupController(PendingSignupRepository pendingSignupRepository) {
        this.pendingSignupRepository = pendingSignupRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest req) {
        if (pendingSignupRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Signup request already pending for this email");
        }
        PendingSignup ps = new PendingSignup(req.name(), req.email(), req.phone());
        pendingSignupRepository.save(ps);
        return ResponseEntity.status(201).body("Signup request received");
    }
}
