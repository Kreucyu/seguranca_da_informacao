package com.exemplo.hmacapi.security;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Guarda os MACs já aceitos até o fim da janela de validade do timestamp.
 * Impede reuso da mesma requisição mesmo dentro da janela.
 * (Em ambiente com várias instâncias, trocar por Redis com TTL.)
 */
@Component
public class ReplayCache {

    private final ConcurrentHashMap<String, Long> seen = new ConcurrentHashMap<>();
    private volatile long lastCleanup = System.currentTimeMillis();

    /** @return true se é a primeira vez que o MAC é visto; false se for repetição. */
    public boolean register(String consumerId, String mac, Duration ttl) {
        long now = System.currentTimeMillis();
        cleanup(now);
        return seen.putIfAbsent(consumerId + ":" + mac, now + ttl.toMillis()) == null;
    }

    private void cleanup(long now) {
        if (now - lastCleanup < 10_000) return;
        lastCleanup = now;
        seen.values().removeIf(expiresAt -> expiresAt < now);
    }
}
