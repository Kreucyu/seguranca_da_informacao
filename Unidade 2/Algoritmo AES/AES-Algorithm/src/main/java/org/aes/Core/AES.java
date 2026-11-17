package org.aes.Core;

import org.aes.Models.CypherData;
import org.aes.Services.DecryptationService;
import org.aes.Services.EncryptationService;
import org.aes.Services.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AES {
    public AES() {}

    private void validate(CypherData cypherData) {
        if(cypherData.getKey() == null || cypherData.getKey().length != 16) {
            throw new IllegalArgumentException("\nChave inválida: deve possuir exatamente 16 bytes.");
        }
        if("CBC".equals(cypherData.getOperationMode())) {
            if(cypherData.getInitializationVector() == null || cypherData.getInitializationVector().length != 16) {
                throw new IllegalArgumentException("\nVetor IV inválido: deve possuir exatamente 16 bytes.");
            }
        }
        if(!"file".equalsIgnoreCase(cypherData.getFinalObjectType())) {
            if(cypherData.getBytedText() == null) {
                throw new IllegalArgumentException("\nTexto de entrada não informado.");
            }
        }
    }

    public byte[] execute(CypherData cypherData) {
        validate(cypherData);
        if("encrypt".equalsIgnoreCase(cypherData.getOperation())) {
            return new EncryptationService().encrypt(cypherData);
        } else {
            return new DecryptationService().decrypt(cypherData);
        }
    }

    public void start(CypherData cypherData) throws IOException {
        validate(cypherData);
        boolean isEncrypt = "encrypt".equalsIgnoreCase(cypherData.getOperation());
        boolean isFile = "file".equalsIgnoreCase(cypherData.getFinalObjectType());

        if(isFile) {
            File outputFile = cypherData.getOutputFile();
            if(outputFile == null) {
                outputFile = FileService.getSaveLocation();
            }
            if(outputFile == null) {
                System.out.println("\nOperação cancelada: nenhum arquivo de saída informado.");
                return;
            }
            cypherData.setOutputFile(outputFile);

            if(isEncrypt) {
                new EncryptationService().encryptFile(cypherData);
                System.out.println("\nArquivo criptografado salvo com sucesso em: " + outputFile.getAbsolutePath());
            } else {
                new DecryptationService().decryptFile(cypherData);
                System.out.println("\nArquivo descriptografado salvo com sucesso em: " + outputFile.getAbsolutePath());
            }
        } else {
            byte[] resultBytes = execute(cypherData);
            if(isEncrypt) {
                System.out.println("\nTexto Criptografado (Hex):");
                System.out.println(toStringTextInHex(resultBytes));
            } else {
                System.out.println("\nTexto Descriptografado:");
                System.out.println(new String(resultBytes, StandardCharsets.UTF_8));
            }
        }
    }

    public static String toStringTextInHex(byte[] bytes) {
        if(bytes == null) return "";
        StringBuilder hex = new StringBuilder();
        for(byte b : bytes) {
            hex.append(String.format("%02X", b & 0xFF));
        }
        return hex.toString();
    }

    public static byte[] fromHexString(String hex) {
        String clean = hex.replaceAll("\\s+", "");
        if(clean.length() % 2 != 0) {
            throw new IllegalArgumentException("String hexadecimal com comprimento ímpar.");
        }
        byte[] data = new byte[clean.length() / 2];
        for(int i = 0; i < clean.length(); i += 2) {
            int hi = Character.digit(clean.charAt(i), 16);
            int lo = Character.digit(clean.charAt(i + 1), 16);
            if(hi < 0 || lo < 0) {
                throw new IllegalArgumentException("Caractere hexadecimal inválido.");
            }
            data[i / 2] = (byte) ((hi << 4) + lo);
        }
        return data;
    }
}
