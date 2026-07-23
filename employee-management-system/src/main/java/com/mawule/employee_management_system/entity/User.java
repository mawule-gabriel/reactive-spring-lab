package com.mawule.employee_management_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Auth-only entity representing a system operator login account.
 * Stores a BCrypt-hashed password and a role ({@code ROLE_ADMIN} or
 * {@code ROLE_USER}); intentionally decoupled from the {@link Employee} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("role")
    private String role;
}
