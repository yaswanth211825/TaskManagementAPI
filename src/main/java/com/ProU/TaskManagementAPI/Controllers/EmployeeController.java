package com.ProU.TaskManagementAPI.Controllers;

import com.ProU.TaskManagementAPI.DTO.CreateEmployeeRequest;
import com.ProU.TaskManagementAPI.DTO.EmployeeDto;
import com.ProU.TaskManagementAPI.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody CreateEmployeeRequest req) {
        EmployeeDto created = employeeService.create(req);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> listAll() {
        return ResponseEntity.ok(employeeService.listAll());
    }
}

