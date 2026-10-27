package com.service.products.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

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
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data != null ? data : new byte[0]));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao calcular hash SHA-256", e);
        }
    }

    private static String hmacHex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao calcular HMAC", e);
        }
    }

    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}

