package com.mawule.employee_management_system.config;

import com.mawule.employee_management_system.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RevokedTokenCleanupTask {

    private static final long ONE_HOUR_MS = 3_600_000L;

    private final RevokedTokenRepository revokedTokenRepository;

    @Scheduled(fixedRate = ONE_HOUR_MS)
    public void deleteExpiredEntries() {
        revokedTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now()).subscribe();
    }
}
