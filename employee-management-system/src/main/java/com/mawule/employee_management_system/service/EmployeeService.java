package com.mawule.employee_management_system.service;

import com.mawule.employee_management_system.dto.request.EmployeeRequest;
import com.mawule.employee_management_system.dto.request.EmployeeSelfUpdateRequest;
import com.mawule.employee_management_system.dto.response.EmployeeResponse;
import com.mawule.employee_management_system.dto.response.EmployeeSummaryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {

    Mono<EmployeeResponse> create(EmployeeRequest request);

    Flux<EmployeeResponse> findAll();

    Flux<EmployeeResponse> findAllByDepartmentId(Long departmentId);

    Mono<EmployeeResponse> findById(Long id);

    Mono<EmployeeResponse> update(Long id, EmployeeRequest request);

    Mono<Void> delete(Long id);

    Mono<EmployeeResponse> findOwnRecord(String email);

    Mono<EmployeeResponse> updateOwnDetails(String email, EmployeeSelfUpdateRequest request);

    Flux<EmployeeSummaryResponse> findColleagues(String email);
}
