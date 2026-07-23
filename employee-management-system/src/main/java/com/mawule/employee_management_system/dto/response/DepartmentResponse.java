package com.mawule.employee_management_system.dto.response;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}
