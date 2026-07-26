package com.mawule.employee_management_system.service.impl;

import com.mawule.employee_management_system.entity.RevokedToken;
import com.mawule.employee_management_system.repository.EmployeeRepository;
import com.mawule.employee_management_system.repository.RevokedTokenRepository;
import com.mawule.employee_management_system.repository.UserRepository;
import com.mawule.employee_management_system.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String SECRET = "a-test-secret-that-is-at-least-32-bytes-long";

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    private AuthServiceImpl authService;
    private JwtUtil jwtUtil;

    private AuthServiceImpl newAuthService() {
        jwtUtil = new JwtUtil(SECRET, 60_000);
        return new AuthServiceImpl(userRepository, employeeRepository, revokedTokenRepository,
                passwordEncoder, authenticationManager, jwtUtil);
    }

    @Test
    void logoutRejectsMissingAuthorizationHeader() {
        authService = newAuthService();

        StepVerifier.create(authService.logout(null))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void logoutRejectsMalformedAuthorizationHeader() {
        authService = newAuthService();

        StepVerifier.create(authService.logout("not-a-bearer-token"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void logoutRejectsInvalidToken() {
        authService = newAuthService();

        StepVerifier.create(authService.logout("Bearer garbage-token"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void logoutSavesRevokedTokenForValidToken() {
        authService = newAuthService();
        String token = jwtUtil.generateToken("jane@company.com", "ROLE_USER");
        when(revokedTokenRepository.save(any(RevokedToken.class)))
                .thenReturn(Mono.just(new RevokedToken(jwtUtil.extractJti(token), jwtUtil.extractExpiration(token))));

        StepVerifier.create(authService.logout("Bearer " + token))
                .verifyComplete();

        verify(revokedTokenRepository).save(argThatHasJti(jwtUtil.extractJti(token)));
    }

    private RevokedToken argThatHasJti(String jti) {
        return org.mockito.ArgumentMatchers.argThat(revokedToken -> {
            assertThat(revokedToken.getJti()).isEqualTo(jti);
            return true;
        });
    }
}
