package vn.edu.uit.devorbit_api.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for revoked JWT tokens (by jti claim).
 * Tokens are tracked until their natural expiration, then auto-evicted.
 */
@Service
public class RevokedTokenStore {

    private final Clock clock;
    private final Map<String, Instant> revokedUntil = new ConcurrentHashMap<>();

    public RevokedTokenStore() {
        this(Clock.systemUTC());
    }

    /**
     * Package-private constructor for testing with a fixed clock.
     */
    RevokedTokenStore(Clock clock) {
        this.clock = clock;
    }

    /**
     * Revoke a token until its natural expiration time.
     */
    public void revoke(String jti, Instant expiresAt) {
        revokedUntil.put(jti, expiresAt);
    }

    /**
     * Legacy revoke method — uses 3-hour window for backward compatibility.
     * New code should use revoke(jti, expiresAt) instead.
     */
    public void revoke(String jti) {
        revokedUntil.put(jti, clock.instant().plusSeconds(3 * 3600));
    }

    /**
     * Check if a token has been revoked and hasn't expired yet.
     * Automatically cleans up entries whose tokens have naturally expired.
     */
    public boolean isRevoked(String jti) {
        Instant expiresAt = revokedUntil.get(jti);
        if (expiresAt == null) return false;
        // Token expired naturally → clean up, no longer revoked
        if (clock.instant().isAfter(expiresAt)) {
            revokedUntil.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * Evict entries whose tokens have naturally expired.
     */
    public void evictExpired() {
        Instant now = clock.instant();
        revokedUntil.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}
