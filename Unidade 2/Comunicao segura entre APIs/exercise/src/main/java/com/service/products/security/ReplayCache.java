package com.service.products.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReplayCache {

    private final ConcurrentHashMap<String, Long> seen = new ConcurrentHashMap<>();
    private volatile long lastCleanup = System.currentTimeMillis();

    public boolean register(String consumerId, String mac, Duration ttl) {
        long now = System.currentTimeMillis();
        cleanup(now);
        return seen.putIfAbsent(consumerId + ": " + mac, now + ttl.toMillis()) == null;
    }

    private void cleanup(long now) {
        if (now - lastCleanup < 10_000) return;
        lastCleanup = now;
        seen.values().removeIf(expiresAt -> expiresAt < now);
    }
}

