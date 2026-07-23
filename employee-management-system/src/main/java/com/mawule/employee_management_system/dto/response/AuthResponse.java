package com.mawule.employee_management_system.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String role,
        long expiresInMs
) {}
