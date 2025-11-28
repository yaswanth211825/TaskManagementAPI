package com.ProU.TaskManagementAPI.Controllers;

import com.ProU.TaskManagementAPI.DTO.*;
import com.ProU.TaskManagementAPI.Services.TaskService;
import com.ProU.TaskManagementAPI.repository.EmployeeRepository;
import com.ProU.TaskManagementAPI.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public TaskController(TaskService taskService, UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody CreateTaskRequest req) {
        return ResponseEntity.ok(taskService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> update(@PathVariable Long id, @RequestBody UpdateTaskRequest req, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

        if (isAdmin) {
            TaskDto updated = taskService.update(id, req);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        }

        // Non-admin: must be employee and can only update status on tasks assigned to them
        String username = authentication.getName();
        var empOpt = employeeRepository.findByEmail(username);
        if (empOpt.isEmpty()) return ResponseEntity.status(403).build();
        var emp = empOpt.get();

        TaskDto existing = taskService.getById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        if (existing.assignedToId() == null || !existing.assignedToId().equals(emp.getId())) {
            return ResponseEntity.status(403).build();
        }

        // ensure only status is being changed
        boolean hasOtherChanges = req.title() != null || req.description() != null || req.assignedToId() != null || req.dueDate() != null;
        if (hasOtherChanges && req.status() == null) {
            return ResponseEntity.status(403).body(null);
        }

        // create a new UpdateTaskRequest that only contains status if employee
        UpdateTaskRequest toApply = new UpdateTaskRequest(null, null, req.status(), null, null);
        TaskDto updated = taskService.update(id, toApply);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> listAll(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

        List<TaskDto> all = taskService.listAll();
        if (isAdmin) {
            return ResponseEntity.ok(all);
        }

        // Non-admin: return only tasks assigned to this employee
        String username = authentication.getName();
        var empOpt = employeeRepository.findByEmail(username);
        if (empOpt.isEmpty()) return ResponseEntity.status(403).build();
        var emp = empOpt.get();

        List<TaskDto> filtered = all.stream()
                .filter(t -> t.assignedToId() != null && t.assignedToId().equals(emp.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        TaskDto t = taskService.getById(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> dashboard() {
        return ResponseEntity.ok(taskService.dashboard());
    }
}

