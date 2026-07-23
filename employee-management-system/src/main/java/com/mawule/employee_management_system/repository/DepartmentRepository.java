package com.mawule.employee_management_system.repository;

import com.mawule.employee_management_system.entity.Department;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {
}
