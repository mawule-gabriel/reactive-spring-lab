package com.mawule.employee_management_system.exception;

public class DepartmentNotEmptyException extends RuntimeException {

    public DepartmentNotEmptyException(Long departmentId) {
        super("Department with id " + departmentId + " cannot be deleted because it still has employees.");
    }
}
