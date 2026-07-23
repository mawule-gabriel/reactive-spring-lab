package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.dto.request.LoginRequest;
import com.mawule.employee_management_system.dto.request.RegisterRequest;
import com.mawule.employee_management_system.dto.response.AuthResponse;
import com.mawule.employee_management_system.entity.User;
import com.mawule.employee_management_system.exception.ResourceNotFoundException;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import com.mawule.employee_management_system.repository.UserRepository;
import com.mawule.employee_management_system.security.JwtUtil;
import com.mawule.employee_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Handles account registration and credential-based login.
 * Registration hashes the password with BCrypt and persists a new user;
 * login delegates verification to the reactive authentication manager and
 * issues a signed JWT on success.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String BEARER = "Bearer";

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.findByEmail(request.email())
                .flatMap(existing -> Mono.<AuthResponse>error(new ResponseStatusException(
                        HttpStatus.CONFLICT, "Email is already registered")))
                .switchIfEmpty(Mono.defer(() -> employeeRepository.findByEmail(request.email())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                "No employee record found for " + request.email()
                                        + ". Ask an administrator to add you as an employee first.")))
                        .flatMap(employee -> {
                            User user = new User();
                            user.setEmail(request.email());
                            user.setPassword(passwordEncoder.encode(request.password()));
                            user.setRole(DEFAULT_ROLE);
                            return userRepository.save(user)
                                    .map(saved -> buildResponse(saved.getEmail(), saved.getRole()));
                        })));
    }

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());
        return authenticationManager.authenticate(credentials)
                .map(authentication -> {
                    String role = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .findFirst()
                            .orElse(DEFAULT_ROLE);
                    return buildResponse(authentication.getName(), role);
                })
                .onErrorMap(error -> !(error instanceof ResponseStatusException),
                        error -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    private AuthResponse buildResponse(String email, String role) {
        String token = jwtUtil.generateToken(email, role);
        return new AuthResponse(token, BEARER, email, role, jwtUtil.getExpiryMs());
    }
}
