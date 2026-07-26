package com.mawule.employee_management_system.repository;

import com.mawule.employee_management_system.entity.RevokedToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends ReactiveCrudRepository<RevokedToken, String> {

    Mono<Void> deleteAllByExpiresAtBefore(LocalDateTime cutoff);
}
