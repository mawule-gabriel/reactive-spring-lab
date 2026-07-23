package com.mawule.employee_management_system.dto.response;

public record EmployeeSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String jobTitle
) {}
