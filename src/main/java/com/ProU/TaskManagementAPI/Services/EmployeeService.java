package com.ProU.TaskManagementAPI.Services;

import com.ProU.TaskManagementAPI.DTO.CreateEmployeeRequest;
import com.ProU.TaskManagementAPI.DTO.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto create(CreateEmployeeRequest req);
    List<EmployeeDto> listAll();
}

