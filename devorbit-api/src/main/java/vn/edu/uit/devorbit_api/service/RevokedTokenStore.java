package vn.edu.uit.devorbit_api.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for revoked JWT tokens (by jti claim).
 * Tokens are automatically evicted after their max lifetime to prevent unbounded growth.
 */
@Service
public class RevokedTokenStore {

    private static final Duration TOKEN_MAX_AGE = Duration.ofHours(3);

    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();

    public void revoke(String jti) {
        revoked.put(jti, Instant.now());
    }

    public boolean isRevoked(String jti) {
        Instant revokedAt = revoked.get(jti);
        if (revokedAt == null) return false;
        // Evict expired entries on access
        if (revokedAt.plus(TOKEN_MAX_AGE).isBefore(Instant.now())) {
            revoked.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * Periodically evict expired entries. Called by a scheduler.
     */
    public void evictExpired() {
        Instant cutoff = Instant.now().minus(TOKEN_MAX_AGE);
        revoked.values().removeIf(revokedAt -> revokedAt.isBefore(cutoff));
    }
}
