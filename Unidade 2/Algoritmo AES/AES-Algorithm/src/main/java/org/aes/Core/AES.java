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
            encryptationService.encrypt(cypherData);
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
}
