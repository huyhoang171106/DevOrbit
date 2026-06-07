package vn.edu.uit.devorbit_api.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpRateLimitService {

    private static final int MAX_REQUESTS = 3;
    private static final Duration WINDOW = Duration.ofMinutes(10);

    // key = "purpose:email" or "purpose:studentCode"
    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    public void check(String key) {
        Deque<Instant> timestamps = attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        Instant now = Instant.now();
        Instant cutoff = now.minus(WINDOW);

        synchronized (timestamps) {
            // remove expired entries
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                throw new BadRequestException("Vui lòng thử lại sau ít phút.");
            }

            timestamps.addLast(now);
        }
    }
}
