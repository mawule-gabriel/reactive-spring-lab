package com.mawule.employee_management_system.dto.request;

public record EmployeeSelfUpdateRequest(
        String firstName,
        String lastName,
        String email
) {}
