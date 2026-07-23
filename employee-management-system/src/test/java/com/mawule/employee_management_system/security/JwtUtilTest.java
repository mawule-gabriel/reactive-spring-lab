package com.mawule.employee_management_system.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "a-test-secret-that-is-at-least-32-bytes-long";

    @Test
    void generatedTokenCarriesUsernameAndRole() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

        String token = jwtUtil.generateToken("jane@company.com", "ROLE_USER");

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("jane@company.com");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ROLE_USER");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void garbageTokenIsInvalid() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

        assertThat(jwtUtil.isTokenValid("not-a-real-token")).isFalse();
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, -1_000);

        String token = jwtUtil.generateToken("jane@company.com", "ROLE_USER");

        assertThat(jwtUtil.isTokenValid(token)).isFalse();
    }

    @Test
    void tokenSignedWithADifferentSecretIsInvalid() {
        JwtUtil signer = new JwtUtil(SECRET, 60_000);
        JwtUtil verifier = new JwtUtil("a-completely-different-secret-32-bytes-plus", 60_000);

        String token = signer.generateToken("jane@company.com", "ROLE_USER");

        assertThat(verifier.isTokenValid(token)).isFalse();
    }
}
