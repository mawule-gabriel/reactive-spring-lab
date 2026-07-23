package com.mawule.employee_management_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * R2DBC entity representing a business department.
 * Mapped to the {@code departments} table; employees reference it via a plain
 * {@code department_id} foreign-key column with no ORM join.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("departments")
public class Department {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
}
