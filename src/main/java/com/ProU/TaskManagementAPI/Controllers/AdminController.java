package com.ProU.TaskManagementAPI.Controllers;

import com.ProU.TaskManagementAPI.DTO.ApproveUserRequest;
import com.ProU.TaskManagementAPI.entity.Employee;
import com.ProU.TaskManagementAPI.entity.PendingSignup;
import com.ProU.TaskManagementAPI.entity.UserAccount;
import com.ProU.TaskManagementAPI.repository.EmployeeRepository;
import com.ProU.TaskManagementAPI.repository.PendingSignupRepository;
import com.ProU.TaskManagementAPI.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PendingSignupRepository pendingSignupRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(PendingSignupRepository pendingSignupRepository, UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.pendingSignupRepository = pendingSignupRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/approvals")
    public ResponseEntity<List<PendingSignup>> listPending() {
        return ResponseEntity.ok(pendingSignupRepository.findAll());
    }

    @PostMapping("/approvals/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id, @RequestBody ApproveUserRequest req) {
        PendingSignup ps = pendingSignupRepository.findById(id).orElse(null);
        if (ps == null) return ResponseEntity.notFound().build();

        if (userRepository.findByUsername(ps.getEmail()).isPresent()) {
            pendingSignupRepository.delete(ps);
            return ResponseEntity.badRequest().body("User already exists; removing pending request");
        }

        // Normalize role
        String role = req.role().toUpperCase();
        if (role.startsWith("ROLE_")) role = role.substring(5);

        // create user account (hash password)
        String hashed = passwordEncoder.encode(req.password());
        UserAccount user = new UserAccount(ps.getEmail(), hashed, role);
        userRepository.save(user);

        // also create employee record if role includes EMPLOYEE or USER
        if ("EMPLOYEE".equals(role) || "USER".equals(role)) {
            Employee emp = new Employee(ps.getName(), ps.getEmail());
            employeeRepository.save(emp);
        }

        pendingSignupRepository.delete(ps);
        return ResponseEntity.ok("User approved");
    }
}
