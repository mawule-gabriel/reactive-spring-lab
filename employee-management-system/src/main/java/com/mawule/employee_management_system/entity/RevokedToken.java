package com.mawule.employee_management_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("revoked_tokens")
public class RevokedToken {

    @Id
    @Column("jti")
    private String jti;

    @Column("expires_at")
    private LocalDateTime expiresAt;
}
