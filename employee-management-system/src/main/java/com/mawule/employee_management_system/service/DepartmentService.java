package com.mawule.employee_management_system.service;

import com.mawule.employee_management_system.dto.request.DepartmentRequest;
import com.mawule.employee_management_system.dto.response.DepartmentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartmentService {

    Mono<DepartmentResponse> create(DepartmentRequest request);

    Flux<DepartmentResponse> findAll();

    Mono<DepartmentResponse> findById(Long id);

    Mono<DepartmentResponse> update(Long id, DepartmentRequest request);

    Mono<Void> delete(Long id);
}
