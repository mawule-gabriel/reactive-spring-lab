package com.mawule.employee_management_system.controller;

import com.mawule.employee_management_system.dto.request.EmployeeRequest;
import com.mawule.employee_management_system.dto.request.EmployeeSelfUpdateRequest;
import com.mawule.employee_management_system.dto.response.EmployeeResponse;
import com.mawule.employee_management_system.dto.response.EmployeeSummaryResponse;
import com.mawule.employee_management_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Flux<EmployeeResponse> findAll(@RequestParam(required = false) Long departmentId) {
        return departmentId == null
                ? employeeService.findAll()
                : employeeService.findAllByDepartmentId(departmentId);
    }

    @GetMapping("/{id}")
    public Mono<EmployeeResponse> findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/me")
    public Mono<EmployeeResponse> findOwnRecord(@AuthenticationPrincipal String email) {
        return employeeService.findOwnRecord(email);
    }

    @PutMapping("/me")
    public Mono<EmployeeResponse> updateOwnRecord(
            @AuthenticationPrincipal String email, @RequestBody EmployeeSelfUpdateRequest request) {
        return employeeService.updateOwnDetails(email, request);
    }

    @GetMapping("/me/colleagues")
    public Flux<EmployeeSummaryResponse> findColleagues(@AuthenticationPrincipal String email) {
        return employeeService.findColleagues(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmployeeResponse> create(@RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @PutMapping("/{id}")
    public Mono<EmployeeResponse> update(@PathVariable Long id, @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return employeeService.delete(id);
    }
}
