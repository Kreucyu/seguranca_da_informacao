package org.blowFish;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Objects;

public class Question {
    void executeEncrypt(String caseNumber, Cipher cipher, byte[] keyValues, byte[] initialText, String algorithm, byte[] initialVector, boolean isFile) {
        try {
            System.out.println("\n=================| CASO " + caseNumber + " |=================");
            SecretKey key = new SecretKeySpec(keyValues, algorithm);
            if(initialVector == null || initialVector.length == 0) cipher.init(Cipher.ENCRYPT_MODE, key);
            else cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initialVector));
            byte[] encryptedText = cipher.doFinal(initialText);
            if(!isFile) {
                showValue(initialText, "\nValor a ser criptografado");
                System.out.println("\nChave a ser utilizada: " + Arrays.toString(keyValues));
                showHexValues(encryptedText);
                System.out.println("\nTamanho do texto cifrado: " + encryptedText.length);
                return;
            }
            Files.write(Path.of("saida.bin"), encryptedText);
            System.out.println("\nArquivo criptografado com sucesso, arquivo criado: \"saida.bin\"");
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 InvalidAlgorithmParameterException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    void showHexValues(byte[] text) {
        int actualPosition = 0;
        System.out.println("\nConteúdo do texto cifrado:\n");
        for(byte bt : text) {
            if(actualPosition >= 8) {
                System.out.println();
                actualPosition = 0;
            }
            System.out.print(bt + " ");
            actualPosition++;
        }
        actualPosition = 0;
        System.out.println();
        System.out.println("\nConteúdo do texto cifrado (hexadecimal):\n");
        for(byte bt : text) {
            if(actualPosition >= 8) {
                System.out.println();
                actualPosition = 0;
            }
            System.out.printf("%02X ", bt);
            actualPosition++;
        }
        System.out.println();
    }

    void showValue(byte[] initialText, String message) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : initialText) {
            stringBuilder.append(b >= 32 && b <= 126 ? (char) b : '.');
            }
        System.out.println(message + " (Texto): " + stringBuilder);
        System.out.println(message + " (Bytes): " + Arrays.toString(initialText));
    }

    void executeDecrypt(String caseNumber, Cipher cipher, byte[] keyValues, byte[] cryptedText, String algorithm, byte[] initialVector, boolean isFile) {
        try {
            System.out.println("\n=================| CASO " + caseNumber + " |=================\n");
            SecretKey key = new SecretKeySpec(keyValues, algorithm);
            if(initialVector == null || initialVector.length == 0) cipher.init(Cipher.DECRYPT_MODE, key);
            else cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initialVector));
            byte[] decryptedText = cipher.doFinal(cryptedText);
            if(!isFile) {
                showValue(cryptedText, "Valor a ser descriptografado");
                System.out.println("\nChave a ser utilizada: " + Arrays.toString(keyValues));
                showValue(decryptedText, "\nValor descriptografado");
                System.out.println("\nTamanho do texto descriptografado: " + decryptedText.length);
                return;
            }
            String fileName = Objects.equals(caseNumber, "14") ? "artigoDescriptografado.txt" : "descriptografado.pdf";
            Files.write(Path.of(fileName), decryptedText);
            System.out.println("Arquivo descriptografado com sucesso, arquivo criado: \"" + fileName + "\"");
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 InvalidAlgorithmParameterException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
