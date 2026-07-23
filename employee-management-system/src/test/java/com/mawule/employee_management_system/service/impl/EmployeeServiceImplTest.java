package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.dto.request.EmployeeRequest;
import com.mawule.employee_management_system.dto.request.EmployeeSelfUpdateRequest;
import com.mawule.employee_management_system.entity.Department;
import com.mawule.employee_management_system.entity.Employee;
import com.mawule.employee_management_system.exception.DuplicateEmailException;
import com.mawule.employee_management_system.exception.ResourceNotFoundException;
import com.mawule.employee_management_system.repository.DepartmentRepository;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private final EmployeeRequest request = new EmployeeRequest(
            "Jane", "Doe", "jane@company.com", "Engineer",
            new BigDecimal("95000.00"), LocalDate.of(2024, 1, 15), 1L);

    @Test
    void createErrorsWhenDepartmentDoesNotExist() {
        when(departmentRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.create(request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void createErrorsWhenEmailAlreadyTaken() {
        Department department = new Department(1L, "Engineering", "Builds stuff", null);
        Employee otherEmployee = new Employee(
                2L, "Bob", "Smith", "jane@company.com", "Manager",
                new BigDecimal("100000"), LocalDate.of(2023, 1, 1), 1L, null);

        when(departmentRepository.findById(1L)).thenReturn(Mono.just(department));
        when(employeeRepository.findByEmail("jane@company.com")).thenReturn(Mono.just(otherEmployee));

        StepVerifier.create(employeeService.create(request))
                .expectError(DuplicateEmailException.class)
                .verify();
    }

    @Test
    void createSavesWhenDepartmentExistsAndEmailIsFree() {
        Department department = new Department(1L, "Engineering", "Builds stuff", null);
        Employee saved = new Employee(
                1L, "Jane", "Doe", "jane@company.com", "Engineer",
                new BigDecimal("95000.00"), LocalDate.of(2024, 1, 15), 1L, null);

        when(departmentRepository.findById(1L)).thenReturn(Mono.just(department));
        when(employeeRepository.findByEmail("jane@company.com")).thenReturn(Mono.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(employeeService.create(request))
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.email()).isEqualTo("jane@company.com");
                    assertThat(response.departmentName()).isEqualTo("Engineering");
                })
                .verifyComplete();
    }

    @Test
    void findOwnRecordErrorsWhenNoEmployeeLinkedToEmail() {
        when(employeeRepository.findByEmail("nobody@company.com")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.findOwnRecord("nobody@company.com"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findColleaguesExcludesSelfAndHidesSensitiveFields() {
        Employee jane = new Employee(
                1L, "Jane", "Doe", "jane@company.com", "Engineer",
                new BigDecimal("95000.00"), LocalDate.of(2024, 1, 15), 1L, null);
        Employee bob = new Employee(
                2L, "Bob", "Smith", "bob@company.com", "Manager",
                new BigDecimal("105000.00"), LocalDate.of(2023, 5, 1), 1L, null);

        when(employeeRepository.findByEmail("jane@company.com")).thenReturn(Mono.just(jane));
        when(employeeRepository.findAllByDepartmentId(1L)).thenReturn(Flux.just(jane, bob));

        StepVerifier.create(employeeService.findColleagues("jane@company.com"))
                .assertNext(colleague -> {
                    assertThat(colleague.id()).isEqualTo(2L);
                    assertThat(colleague.firstName()).isEqualTo("Bob");
                    assertThat(colleague.jobTitle()).isEqualTo("Manager");
                })
                .verifyComplete();
    }

    @Test
    void updateOwnDetailsOnlyChangesNameAndEmail() {
        Employee jane = new Employee(
                1L, "Jane", "Doe", "jane@company.com", "Engineer",
                new BigDecimal("95000.00"), LocalDate.of(2024, 1, 15), 1L, null);
        Department department = new Department(1L, "Engineering", "Builds stuff", null);
        EmployeeSelfUpdateRequest selfUpdate = new EmployeeSelfUpdateRequest("Janet", "Doe", "jane@company.com");

        when(employeeRepository.findByEmail("jane@company.com")).thenReturn(Mono.just(jane));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(jane));
        when(departmentRepository.findById(1L)).thenReturn(Mono.just(department));

        StepVerifier.create(employeeService.updateOwnDetails("jane@company.com", selfUpdate))
                .assertNext(response -> {
                    assertThat(response.firstName()).isEqualTo("Janet");
                    assertThat(response.jobTitle()).isEqualTo("Engineer");
                    assertThat(response.salary()).isEqualByComparingTo("95000.00");
                })
                .verifyComplete();
    }
}
