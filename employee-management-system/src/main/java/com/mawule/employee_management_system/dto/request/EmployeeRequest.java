package com.mawule.employee_management_system.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        String firstName,
        String lastName,
        String email,
        String jobTitle,
        BigDecimal salary,
        LocalDate hireDate,
        Long departmentId
) {}
