package vn.edu.uit.devorbit_api.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter for login attempts.
 * Tracks both student code / username AND IP address to prevent brute-force attacks.
 *
 * Limits: 5 attempts per 15 minutes per key (studentCode + IP).
 */
@Service
public class LoginRateLimitService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final Duration IP_BLOCK_DURATION = Duration.ofMinutes(30);

    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();
    private final Map<String, Instant> ipBlacklist = new ConcurrentHashMap<>();

    public void check(String studentCode, String ipAddress) {
        String userKey = "user:" + studentCode;
        String ipKey = "ip:" + ipAddress;

        // Check IP blacklist
        Instant ipBlockedUntil = ipBlacklist.get(ipKey);
        if (ipBlockedUntil != null && Instant.now().isBefore(ipBlockedUntil)) {
            throw new UnauthorizedException("Too many login attempts. Try again later.");
        }

        Instant now = Instant.now();

        // Check user-level rate limit
        checkKey(userKey, now);
        // Check IP-level rate limit
        checkKey(ipKey, now);
    }

    /**
     * Record a failed attempt. Returns true if the threshold is now exceeded (blocked).
     */
    public boolean recordFailure(String studentCode, String ipAddress) {
        String userKey = "user:" + studentCode;
        String ipKey = "ip:" + ipAddress;
        Instant now = Instant.now();

        boolean userBlocked = recordAndCheck(userKey, now);
        boolean ipBlocked = recordAndCheck(ipKey, now);

        if (ipBlocked) {
            ipBlacklist.put(ipKey, now.plus(IP_BLOCK_DURATION));
        }

        return userBlocked || ipBlocked;
    }

    /**
     * Clear rate limit records on successful login.
     */
    public void onSuccess(String studentCode, String ipAddress) {
        attempts.remove("user:" + studentCode);
        attempts.remove("ip:" + ipAddress);
        ipBlacklist.remove("ip:" + ipAddress);
    }

    private void checkKey(String key, Instant now) {
        Deque<Instant> timestamps = attempts.get(key);
        if (timestamps == null) return;

        Instant cutoff = now.minus(WINDOW);
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= MAX_ATTEMPTS) {
                throw new UnauthorizedException("Too many login attempts. Try again later.");
            }
        }
    }

    private boolean recordAndCheck(String key, Instant now) {
        Deque<Instant> timestamps = attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        Instant cutoff = now.minus(WINDOW);

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }
            timestamps.addLast(now);
            return timestamps.size() >= MAX_ATTEMPTS;
        }
    }
}
