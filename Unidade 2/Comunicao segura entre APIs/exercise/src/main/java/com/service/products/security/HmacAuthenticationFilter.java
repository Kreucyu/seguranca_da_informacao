package com.service.products.security;

import com.service.products.config.HmacProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Component
public class HmacAuthenticationFilter extends OncePerRequestFilter {

    public static final String H_CONSUMER = "X-Consumer-Id";
    public static final String H_TIMESTAMP = "X-Timestamp";
    public static final String H_SIGNATURE = "X-Signature";
    public static final String H_MAC = "X-Mac";

    @Autowired
    private HmacProperties props;

    @Autowired
    private ReplayCache replayCache;

    public HmacAuthenticationFilter() {
    }

    public HmacAuthenticationFilter(HmacProperties props, ReplayCache replayCache) {
        this.props = props;
        this.replayCache = replayCache;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String consumerId = request.getHeader(H_CONSUMER);
        String timestamp = request.getHeader(H_TIMESTAMP);
        String signature = request.getHeader(H_SIGNATURE);

        if (signature == null || signature.isBlank()) {
            signature = request.getHeader(H_MAC);
        }

        if (isBlank(consumerId) || isBlank(timestamp) || isBlank(signature)) {
            reject(response, "Cabeçalhos de autenticação ausentes");
            return;
        }

        String secret = props.getConsumers().get(consumerId);
        if (secret == null) {
            reject(response, "Consumidor não autorizado");
            return;
        }

        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            reject(response, "Timestamp inválido");
            return;
        }

        long drift = Math.abs(Instant.now().getEpochSecond() - ts);
        if (drift > props.getToleranceSeconds()) {
            reject(response, "Timestamp fora da janela permitida (possível replay attack)");
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
            reject(response, "Código MAC inválido");
            return;
        }

        Duration ttl = Duration.ofSeconds(props.getToleranceSeconds() * 2 + 1);
        if (!replayCache.register(consumerId, expected, ttl)) {
            reject(response, "Requisição repetida (replay attack detectado)");
            return;
        }

        chain.doFilter(new CachedBodyRequestWrapper(request, body), response);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("WWW-Authenticate", "HMAC");
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}

