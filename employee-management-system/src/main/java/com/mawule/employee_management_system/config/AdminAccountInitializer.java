package com.mawule.employee_management_system.config;

import com.mawule.employee_management_system.entity.User;
import com.mawule.employee_management_system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Bootstraps the initial {@code ROLE_ADMIN} account at startup from the
 * {@code EMS_ADMIN_EMAIL} / {@code EMS_ADMIN_PASSWORD} environment variables.
 * Credentials are never committed: if either variable is absent the step is
 * skipped, and an existing account with the same email is left untouched so
 * the runner is safe to execute on every boot.
 */
@Slf4j
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminAccountInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("Admin bootstrap skipped: EMS_ADMIN_EMAIL / EMS_ADMIN_PASSWORD not set");
            return;
        }
        if (adminPassword.length() < MIN_PASSWORD_LENGTH) {
            log.warn("Admin bootstrap skipped: EMS_ADMIN_PASSWORD must be at least {} characters", MIN_PASSWORD_LENGTH);
            return;
        }
        userRepository.findByEmail(adminEmail)
                .doOnNext(existing -> log.info("Admin bootstrap skipped: account already exists for {}", adminEmail))
                .switchIfEmpty(Mono.defer(this::createAdmin))
                .block();
    }

    private Mono<User> createAdmin() {
        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(ADMIN_ROLE);
        return userRepository.save(admin)
                .doOnNext(saved -> log.info("Admin bootstrap created account for {}", saved.getEmail()));
    }
}
