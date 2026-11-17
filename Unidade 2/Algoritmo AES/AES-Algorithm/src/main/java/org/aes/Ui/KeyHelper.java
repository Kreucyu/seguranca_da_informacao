package org.aes.Ui;

import org.aes.Core.AES;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class KeyHelper {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] parseKeyOrIV(String input, String fieldName) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode estar vazia.");
        }
        String trimmed = input.trim();

        if (trimmed.contains(",")) {
            String[] parts = trimmed.split(",");
            if (parts.length != 16) {
                throw new IllegalArgumentException(fieldName + " deve conter exatamente 16 valores separados por vírgula (encontrado: " + parts.length + ").");
            }
            byte[] bytes = new byte[16];
            for (int i = 0; i < 16; i++) {
                try {
                    int val = Integer.parseInt(parts[i].trim());
                    if (val < 0 || val > 255) {
                        throw new IllegalArgumentException("Cada valor de " + fieldName + " deve estar entre 0 e 255 (encontrado: " + val + ").");
                    }
                    bytes[i] = (byte) val;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valor numérico inválido em " + fieldName + ": " + parts[i]);
                }
            }
            return bytes;
        }

        String cleanHex = trimmed.replaceAll("\\s+", "");
        if (cleanHex.length() == 32 && cleanHex.matches("^[0-9a-fA-F]{32}$")) {
            return AES.fromHexString(cleanHex);
        }

        byte[] utf8Bytes = trimmed.getBytes(StandardCharsets.UTF_8);
        if (utf8Bytes.length == 16) {
            return utf8Bytes;
        }

        throw new IllegalArgumentException(fieldName + " deve possuir exatamente 16 bytes (128 bits).\n\n" +
                "Formatos aceitos:\n" +
                "• Texto de 16 caracteres (ex: 'chave_secreta_16')\n" +
                "• 16 números entre 0 e 255 separados por vírgula (ex: 1,2,3...)\n" +
                "• 32 caracteres em Hexadecimal (ex: 2b7e151628aed2a6abf7158809cf4f3c)");
    }

    public static byte[] parseInputText(String input, boolean isDecrypt) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("O texto de entrada não pode estar vazio.");
        }
        if (isDecrypt) {
            String clean = input.trim().replaceAll("\\s+", "");
            if (clean.length() >= 32 && clean.length() % 32 == 0 && clean.matches("^[0-9a-fA-F]+$")) {
                return AES.fromHexString(clean);
            }
        }
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static String generateRandomKeyOrIV() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return AES.toStringTextInHex(bytes);
    }
}
