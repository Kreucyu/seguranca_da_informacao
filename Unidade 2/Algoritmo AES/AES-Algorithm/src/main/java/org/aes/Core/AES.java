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

    public void start(CypherData cypherData) throws IOException {
        if(Objects.equals(cypherData.getOperationMode(), "CBC")) {
            if(cypherData.getInitializationVector() == null || cypherData.getBytedText() == null) {
                throw new RuntimeException("\nValores inválidos");
            }
        }
        if(cypherData.getOperation().equals("encrypt")) {
            EncryptationService encryptationService = new EncryptationService();
            byte[] encryptedText = encryptationService.encrypt(cypherData);
            if(cypherData.getFinalObjectType().equals("file")) {
                File file = cypherData.getOutputFile() != null ? cypherData.getOutputFile() : FileService.getSaveLocation();
                if(file != null) {
                    Files.write(file.toPath(), encryptedText);
                    System.out.println("\nArquivo criptografado salvo com sucesso em: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("\nTexto Criptografado (Hex):");
                System.out.println(toStringTextInHex(encryptedText));
            }
        } else {
            DecryptationService decryptationService = new DecryptationService();
            byte[] decryptedText = decryptationService.decrypt(cypherData);
            if(cypherData.getFinalObjectType().equals("file")) {
                File file = cypherData.getOutputFile() != null ? cypherData.getOutputFile() : FileService.getSaveLocation();
                if(file != null) {
                    Files.write(file.toPath(), decryptedText);
                    System.out.println("\nArquivo descriptografado salvo com sucesso em: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("\nTexto Descriptografado:");
                System.out.println(new String(decryptedText, StandardCharsets.UTF_8));
            }
        }
    }

    private String toStringTextInHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b & 0xFF));
        }
        return hex.toString();
    }
}
