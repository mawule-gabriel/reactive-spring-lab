package com.mawule.employee_management_system.repository;

import com.mawule.employee_management_system.entity.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    Flux<Employee> findAllByDepartmentId(Long departmentId);

    Mono<Long> countByDepartmentId(Long departmentId);

    Mono<Employee> findByEmail(String email);
}
