package com.mawule.employee_management_system.service;

import com.mawule.employee_management_system.dto.request.LoginRequest;
import com.mawule.employee_management_system.dto.request.RegisterRequest;
import com.mawule.employee_management_system.dto.response.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> register(RegisterRequest request);

    Mono<AuthResponse> login(LoginRequest request);
}
