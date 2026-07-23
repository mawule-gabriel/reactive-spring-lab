package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.dto.request.DepartmentRequest;
import com.mawule.employee_management_system.dto.response.DepartmentResponse;
import com.mawule.employee_management_system.entity.Department;
import com.mawule.employee_management_system.exception.DepartmentNotEmptyException;
import com.mawule.employee_management_system.exception.ResourceNotFoundException;
import com.mawule.employee_management_system.repository.DepartmentRepository;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import com.mawule.employee_management_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Mono<DepartmentResponse> create(DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.name());
        department.setDescription(request.description());
        return departmentRepository.save(department)
                .map(this::toResponse);
    }

    @Override
    public Flux<DepartmentResponse> findAll() {
        return departmentRepository.findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> findById(Long id) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Department not found with id: " + id)))
                .map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> update(Long id, DepartmentRequest request) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Department not found with id: " + id)))
                .flatMap(existing -> {
                    existing.setName(request.name());
                    existing.setDescription(request.description());
                    return departmentRepository.save(existing);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Department not found with id: " + id)))
                .flatMap(dept -> employeeRepository.countByDepartmentId(id))
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new DepartmentNotEmptyException(id));
                    }
                    return departmentRepository.deleteById(id);
                });
    }

    private DepartmentResponse toResponse(Department dept) {
        return new DepartmentResponse(
                dept.getId(),
                dept.getName(),
                dept.getDescription(),
                dept.getCreatedAt()
        );
    }
}
