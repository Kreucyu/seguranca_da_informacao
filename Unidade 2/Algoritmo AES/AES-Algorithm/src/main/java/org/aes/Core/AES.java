package org.aes.Core;

import org.aes.Models.CypherData;
import org.aes.Services.DecryptationService;
import org.aes.Services.EncryptationService;
import org.aes.Services.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class AES {
    public AES() {}

    public byte[] execute(CypherData cypherData) {
        if(Objects.equals(cypherData.getOperationMode(), "CBC")) {
            if(cypherData.getInitializationVector() == null || cypherData.getBytedText() == null) {
                throw new RuntimeException("Valores inválidos: Vetor IV ou texto não informados.");
            }
        }
        if("encrypt".equalsIgnoreCase(cypherData.getOperation())) {
            EncryptationService encryptationService = new EncryptationService();
            return encryptationService.encrypt(cypherData);
        } else {
            DecryptationService decryptationService = new DecryptationService();
            return decryptationService.decrypt(cypherData);
        }
    }

    public void start(CypherData cypherData) throws IOException {
        byte[] resultBytes = execute(cypherData);
        if("encrypt".equalsIgnoreCase(cypherData.getOperation())) {
            if("file".equalsIgnoreCase(cypherData.getFinalObjectType())) {
                File file = cypherData.getOutputFile() != null ? cypherData.getOutputFile() : FileService.getSaveLocation();
                if(file != null) {
                    Files.write(file.toPath(), resultBytes);
                    System.out.println("\nArquivo criptografado salvo com sucesso em: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("\nTexto Criptografado (Hex):");
                System.out.println(toStringTextInHex(resultBytes));
            }
        } else {
            if("file".equalsIgnoreCase(cypherData.getFinalObjectType())) {
                File file = cypherData.getOutputFile() != null ? cypherData.getOutputFile() : FileService.getSaveLocation();
                if(file != null) {
                    Files.write(file.toPath(), resultBytes);
                    System.out.println("\nArquivo descriptografado salvo com sucesso em: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("\nTexto Descriptografado:");
                System.out.println(new String(resultBytes, StandardCharsets.UTF_8));
            }
        }
    }

    public static String toStringTextInHex(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b & 0xFF));
        }
        return hex.toString();
    }

    public static byte[] fromHexString(String hex) {
        String clean = hex.replaceAll("\\s+", "");
        if (clean.length() % 2 != 0) {
            throw new IllegalArgumentException("String hexadecimal com comprimento ímpar.");
        }
        byte[] data = new byte[clean.length() / 2];
        for (int i = 0; i < clean.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(clean.charAt(i), 16) << 4)
                                 + Character.digit(clean.charAt(i+1), 16));
        }
        return data;
    }
}
