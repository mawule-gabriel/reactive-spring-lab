package com.mawule.employee_management_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * Enables Spring Data R2DBC auditing so that {@code @CreatedDate} fields
 * on entities are populated automatically on insert.
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {
}
