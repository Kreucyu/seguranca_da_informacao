package com.exemplo.hmacapi.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.exemplo.hmacapi.config.HmacProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Autenticação HMAC. Cabeçalhos exigidos em toda requisição /api/**:
 *   X-Consumer-Id : identificador do consumidor
 *   X-Timestamp   : epoch em segundos
 *   X-Signature   : HMAC-SHA256 (hex) da string canônica (ver HmacSigner)
 * Qualquer falha => 401 e a operação NÃO é executada.
 */
@Component
public class HmacAuthenticationFilter extends OncePerRequestFilter {

    public static final String H_CONSUMER = "X-Consumer-Id";
    public static final String H_TIMESTAMP = "X-Timestamp";
    public static final String H_SIGNATURE = "X-Signature";

    private static final Logger log = LoggerFactory.getLogger(HmacAuthenticationFilter.class);

    private final HmacProperties props;
    private final ReplayCache replayCache;

    public HmacAuthenticationFilter(HmacProperties props, ReplayCache replayCache) {
        this.props = props;
        this.replayCache = replayCache;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String consumerId = request.getHeader(H_CONSUMER);
        String timestamp = request.getHeader(H_TIMESTAMP);
        String signature = request.getHeader(H_SIGNATURE);

        if (isBlank(consumerId) || isBlank(timestamp) || isBlank(signature)) {
            reject(response, "Cabeçalhos de autenticação ausentes", "headers ausentes");
            return;
        }

        String secret = props.getConsumers().get(consumerId);
        if (secret == null) {
            reject(response, "Credenciais inválidas", "consumidor desconhecido: " + sanitize(consumerId));
            return;
        }

        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            reject(response, "Timestamp inválido", "timestamp não numérico");
            return;
        }

        long drift = Math.abs(Instant.now().getEpochSecond() - ts);
        if (drift > props.getToleranceSeconds()) {
            reject(response, "Timestamp fora da janela permitida (possível replay)",
                    "timestamp fora da janela, diferença de " + drift + "s, consumidor=" + consumerId);
            return;
        }

        byte[] body = request.getInputStream().readNBytes(props.getMaxBodyBytes() + 1);
        if (body.length > props.getMaxBodyBytes()) {
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            return;
        }

        String path = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        String expected = HmacSigner.sign(secret, request.getMethod(), path, timestamp, consumerId, body);

        if (!HmacSigner.constantTimeEquals(expected, signature.toLowerCase())) {
            reject(response, "Credenciais inválidas", "MAC inválido, consumidor=" + consumerId);
            return;
        }

        // Só registra no cache depois de validar o MAC (evita "envenenar" o cache).
        Duration ttl = Duration.ofSeconds(props.getToleranceSeconds() * 2 + 1);
        if (!replayCache.register(consumerId, expected, ttl)) {
            reject(response, "Requisição repetida (replay detectado)", "replay dentro da janela, consumidor=" + consumerId);
            return;
        }

        log.info("Requisição autenticada: consumidor={} {} {}", consumerId, request.getMethod(), path);
        chain.doFilter(new CachedBodyRequestWrapper(request, body), response);
    }

    private void reject(HttpServletResponse response, String message, String logReason) throws IOException {
        log.warn("401 - {}", logReason);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("WWW-Authenticate", "HMAC");
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String sanitize(String s) { return s.replaceAll("[\\r\\n]", "_"); }
}
