package com.exemplo.hmacapi.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Regras de assinatura (usadas pelo servidor e também por clientes/testes).
 *
 * String canônica assinada (separada por '\n'):
 *   METHOD
 *   PATH[?QUERY]
 *   TIMESTAMP (epoch em segundos)
 *   CONSUMER_ID
 *   SHA256_HEX(BODY)
 *
 * MAC = HMAC-SHA256(secret, string canônica), em hexadecimal minúsculo.
 */
public final class HmacSigner {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private HmacSigner() {}

    public static String sign(String secret, String method, String pathWithQuery,
                              String timestamp, String consumerId, byte[] body) {
        String canonical = String.join("\n",
                method.toUpperCase(),
                pathWithQuery,
                timestamp,
                consumerId,
                sha256Hex(body));
        return hmacHex(secret, canonical);
    }

    public static String sha256Hex(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hmacHex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Comparação em tempo constante (evita timing attack). */
    public static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
