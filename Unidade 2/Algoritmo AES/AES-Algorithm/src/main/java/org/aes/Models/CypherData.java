package org.aes.Models;

public record CypherData(
        String operation,
        String finalObjectType,
        byte[] bytedText,
        byte[] key,
        String operationMode,
        byte[] initializationVector
) {
}
