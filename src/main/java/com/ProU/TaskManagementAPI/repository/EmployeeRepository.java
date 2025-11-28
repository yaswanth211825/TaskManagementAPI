package com.ProU.TaskManagementAPI.repository;

import com.ProU.TaskManagementAPI.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	java.util.Optional<Employee> findByEmail(String email);
}

