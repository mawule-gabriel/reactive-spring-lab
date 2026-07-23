package com.mawule.employee_management_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * R2DBC entity representing an employee business record.
 * The {@code department_id} column is a plain FK value; any join to fetch
 * department details is performed explicitly in the service layer. This entity
 * is intentionally decoupled from the {@link User} auth entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("employees")
public class Employee {

    @Id
    private Long id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email")
    private String email;

    @Column("job_title")
    private String jobTitle;

    @Column("salary")
    private BigDecimal salary;

    @Column("hire_date")
    private LocalDate hireDate;

    @Column("department_id")
    private Long departmentId;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
}
