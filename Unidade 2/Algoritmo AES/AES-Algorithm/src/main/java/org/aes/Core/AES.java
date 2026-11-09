package org.aes.Core;

import org.aes.Models.CypherData;
import org.aes.Services.DecryptationService;
import org.aes.Services.EncryptationService;

import java.util.Objects;

public class AES {
    public AES() {}

    public void start(CypherData cypherData) {
        if(Objects.equals(cypherData.getOperationMode(), "CBC")) {
            if(cypherData.getInitializationVector() == null || cypherData.getBytedText() == null) {
                throw new RuntimeException("\nValores inválidos");
            }
            cypherData.setBytedText(doXORwithIv(cypherData.getBytedText(), cypherData.getKey()));
        }
        if(cypherData.getOperation().equals("encrypt")) {
            EncryptationService encryptationService = new EncryptationService();
            byte[] ecryptedText = encryptationService.encrypt(cypherData);
            toStringTextInHex(ecryptedText);
        } else {
            DecryptationService decryptationService = new DecryptationService();
            decryptationService.decrypt(cypherData);
        }
    }

    private byte[] doXORwithIv(byte[] text, byte[] iv) {
        byte[] newText = new byte[text.length];
        for(int i = 0; i < text.length; i++) {
            newText[i] = (byte) (text[i] ^ iv[i % iv.length]);
        }
        return newText;
    }

    private String toStringTextInHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b & 0xFF));
        }
        return hex.toString();
    }
}
