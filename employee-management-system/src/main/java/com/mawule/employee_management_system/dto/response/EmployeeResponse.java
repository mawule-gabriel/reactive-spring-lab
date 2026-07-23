package com.mawule.employee_management_system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String jobTitle,
        BigDecimal salary,
        LocalDate hireDate,
        Long departmentId,
        String departmentName,
        LocalDateTime createdAt
) {}
