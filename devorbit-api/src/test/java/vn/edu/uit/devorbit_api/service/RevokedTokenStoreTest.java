package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevokedTokenStoreTest {

    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void revokedTokenShouldStayRevokedBeforeExpiration() {
        Clock clock = Clock.fixed(BASE_TIME, ZoneId.of("UTC"));
        RevokedTokenStore store = new RevokedTokenStore(clock);

        Instant tokenExpiresAt = BASE_TIME.plusSeconds(24 * 3600); // expires in 24h
        store.revoke("jti-1", tokenExpiresAt);

        assertTrue(store.isRevoked("jti-1"), "Token should be revoked immediately after revocation");
    }

    @Test
    void revokedTokenShouldAutoCleanAfterTokenExpiration() {
        // Token expires at BASE_TIME + 2h
        Instant tokenExpiresAt = BASE_TIME.plusSeconds(2 * 3600);

        // Create store at time AFTER token expiration (BASE_TIME + 3h)
        Clock clockAfterExpiry = Clock.fixed(BASE_TIME.plusSeconds(3 * 3600), ZoneId.of("UTC"));
        RevokedTokenStore store = new RevokedTokenStore(clockAfterExpiry);

        // Revoke at expiry time (store clock is already past expiry)
        store.revoke("jti-2", tokenExpiresAt);

        // isRevoked should detect token expired and clean up
        assertFalse(store.isRevoked("jti-2"), "Revoked token should be cleaned after its natural expiration");
    }

    @Test
    void revokedTokenShouldNotBeCleanedBeforeTokenExpiration() {
        // Token expires at BASE_TIME + 6h
        Instant tokenExpiresAt = BASE_TIME.plusSeconds(6 * 3600);

        // Create store at BASE_TIME (well before expiry)
        Clock clock = Clock.fixed(BASE_TIME, ZoneId.of("UTC"));
        RevokedTokenStore store = new RevokedTokenStore(clock);

        store.revoke("jti-3", tokenExpiresAt);
        store.evictExpired();

        assertTrue(store.isRevoked("jti-3"), "Token should NOT be cleaned before its expiration");
    }
}
