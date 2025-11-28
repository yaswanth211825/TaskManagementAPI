package com.ProU.TaskManagementAPI.Services.impl;

import com.ProU.TaskManagementAPI.DTO.CreateEmployeeRequest;
import com.ProU.TaskManagementAPI.DTO.EmployeeDto;
import com.ProU.TaskManagementAPI.Services.EmployeeService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MockEmployeeService implements EmployeeService {

    private final List<EmployeeDto> store = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public EmployeeDto create(CreateEmployeeRequest req) {
        EmployeeDto e = new EmployeeDto(idGen.getAndIncrement(), req.name(), req.email(), Instant.now());
        store.add(e);
        return e;
    }

    @Override
    public List<EmployeeDto> listAll() {
        return new ArrayList<>(store);
    }
}
