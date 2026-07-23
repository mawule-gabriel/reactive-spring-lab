package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.dto.request.DepartmentRequest;
import com.mawule.employee_management_system.entity.Department;
import com.mawule.employee_management_system.exception.DepartmentNotEmptyException;
import com.mawule.employee_management_system.exception.ResourceNotFoundException;
import com.mawule.employee_management_system.repository.DepartmentRepository;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void createSavesAndReturnsDepartment() {
        Department saved = new Department(1L, "Engineering", "Builds stuff", null);
        when(departmentRepository.save(any(Department.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(departmentService.create(new DepartmentRequest("Engineering", "Builds stuff")))
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.name()).isEqualTo("Engineering");
                })
                .verifyComplete();
    }

    @Test
    void findByIdErrorsWhenDepartmentMissing() {
        when(departmentRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.findById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void deleteRefusesWhenDepartmentStillHasEmployees() {
        Department department = new Department(1L, "Engineering", "Builds stuff", null);
        when(departmentRepository.findById(1L)).thenReturn(Mono.just(department));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(Mono.just(2L));

        StepVerifier.create(departmentService.delete(1L))
                .expectError(DepartmentNotEmptyException.class)
                .verify();
    }

    @Test
    void deleteSucceedsWhenDepartmentIsEmpty() {
        Department department = new Department(1L, "Engineering", "Builds stuff", null);
        when(departmentRepository.findById(1L)).thenReturn(Mono.just(department));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(Mono.just(0L));
        when(departmentRepository.deleteById(eq(1L))).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.delete(1L))
                .verifyComplete();
    }
}
