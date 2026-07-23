package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.dto.request.EmployeeRequest;
import com.mawule.employee_management_system.dto.request.EmployeeSelfUpdateRequest;
import com.mawule.employee_management_system.dto.response.EmployeeResponse;
import com.mawule.employee_management_system.dto.response.EmployeeSummaryResponse;
import com.mawule.employee_management_system.entity.Department;
import com.mawule.employee_management_system.entity.Employee;
import com.mawule.employee_management_system.exception.DuplicateEmailException;
import com.mawule.employee_management_system.exception.ResourceNotFoundException;
import com.mawule.employee_management_system.repository.DepartmentRepository;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import com.mawule.employee_management_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Mono<EmployeeResponse> create(EmployeeRequest request) {
        return validateDepartmentExists(request.departmentId())
                .flatMap(dept -> validateEmailAvailable(request.email(), null)
                        .then(Mono.defer(() -> {
                            Employee employee = new Employee();
                            employee.setFirstName(request.firstName());
                            employee.setLastName(request.lastName());
                            employee.setEmail(request.email());
                            employee.setJobTitle(request.jobTitle());
                            employee.setSalary(request.salary());
                            employee.setHireDate(request.hireDate());
                            employee.setDepartmentId(request.departmentId());
                            return employeeRepository.save(employee)
                                    .map(saved -> toResponse(saved, dept.getName()));
                        })));
    }

    @Override
    public Flux<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .flatMap(this::enrichWithDepartment);
    }

    @Override
    public Flux<EmployeeResponse> findAllByDepartmentId(Long departmentId) {
        return employeeRepository.findAllByDepartmentId(departmentId)
                .flatMap(this::enrichWithDepartment);
    }

    @Override
    public Mono<EmployeeResponse> findById(Long id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Employee not found with id: " + id)))
                .flatMap(this::enrichWithDepartment);
    }

    @Override
    public Mono<EmployeeResponse> update(Long id, EmployeeRequest request) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Employee not found with id: " + id)))
                .flatMap(existing -> validateDepartmentExists(request.departmentId())
                        .flatMap(dept -> validateEmailAvailable(request.email(), id)
                                .then(Mono.defer(() -> {
                                    existing.setFirstName(request.firstName());
                                    existing.setLastName(request.lastName());
                                    existing.setEmail(request.email());
                                    existing.setJobTitle(request.jobTitle());
                                    existing.setSalary(request.salary());
                                    existing.setHireDate(request.hireDate());
                                    existing.setDepartmentId(request.departmentId());
                                    return employeeRepository.save(existing)
                                            .map(saved -> toResponse(saved, dept.getName()));
                                }))));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Employee not found with id: " + id)))
                .flatMap(employee -> employeeRepository.deleteById(employee.getId()));
    }

    @Override
    public Mono<EmployeeResponse> findOwnRecord(String email) {
        return findEmployeeByEmail(email)
                .flatMap(this::enrichWithDepartment);
    }

    @Override
    public Mono<EmployeeResponse> updateOwnDetails(String email, EmployeeSelfUpdateRequest request) {
        return findEmployeeByEmail(email)
                .flatMap(existing -> validateEmailAvailable(request.email(), existing.getId())
                        .then(Mono.defer(() -> {
                            existing.setFirstName(request.firstName());
                            existing.setLastName(request.lastName());
                            existing.setEmail(request.email());
                            return employeeRepository.save(existing)
                                    .flatMap(this::enrichWithDepartment);
                        })));
    }

    @Override
    public Flux<EmployeeSummaryResponse> findColleagues(String email) {
        return findEmployeeByEmail(email)
                .flatMapMany(self -> employeeRepository.findAllByDepartmentId(self.getDepartmentId())
                        .filter(colleague -> !colleague.getId().equals(self.getId()))
                        .map(this::toSummary));
    }

    private Mono<Employee> findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("No employee record is linked to your account.")));
    }

    /**
     * Validates that a department with the given id exists.
     * Returns the Department on success so callers can reuse it (e.g. for the name).
     * Signals ResourceNotFoundException if not found.
     */
    private Mono<Department> validateDepartmentExists(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Department not found with id: " + departmentId)));
    }

    private Mono<Void> validateEmailAvailable(String email, Long excludeEmployeeId) {
        return employeeRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(excludeEmployeeId))
                .flatMap(existing -> Mono.<Void>error(new DuplicateEmailException(email)))
                .then();
    }

    /**
     * Enriches an Employee with its department name via a reactive flatMap join.
     * If the department row has been removed since the employee was saved,
     * departmentName defaults to null rather than failing the entire stream.
     */
    private Mono<EmployeeResponse> enrichWithDepartment(Employee employee) {
        return departmentRepository.findById(employee.getDepartmentId())
                .map(dept -> toResponse(employee, dept.getName()))
                .defaultIfEmpty(toResponse(employee, null));
    }

    private EmployeeSummaryResponse toSummary(Employee emp) {
        return new EmployeeSummaryResponse(
                emp.getId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getJobTitle()
        );
    }

    private EmployeeResponse toResponse(Employee emp, String departmentName) {
        return new EmployeeResponse(
                emp.getId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getJobTitle(),
                emp.getSalary(),
                emp.getHireDate(),
                emp.getDepartmentId(),
                departmentName,
                emp.getCreatedAt()
        );
    }
}
