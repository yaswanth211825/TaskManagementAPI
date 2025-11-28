package com.ProU.TaskManagementAPI.service;

import com.ProU.TaskManagementAPI.DTO.CreateEmployeeRequest;
import com.ProU.TaskManagementAPI.DTO.EmployeeDto;
import com.ProU.TaskManagementAPI.Services.EmployeeService;
import com.ProU.TaskManagementAPI.entity.Employee;
import com.ProU.TaskManagementAPI.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDto create(CreateEmployeeRequest req) {
        Employee e = new Employee(req.name(), req.email());
        Employee saved = employeeRepository.save(e);
        return new EmployeeDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getCreatedAt());
    }

    @Override
    public List<EmployeeDto> listAll() {
        return employeeRepository.findAll().stream()
                .map(e -> new EmployeeDto(e.getId(), e.getName(), e.getEmail(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }
}

