package org.blowFish;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        Question question = new Question();
        try {
            question.executeEncrypt("1",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "FURB".getBytes(),
                    "Blowfish",
                    null, false);

            question.executeEncrypt("2",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "COMPUTADOR".getBytes(),
                    "Blowfish",
                    null, false);

            question.executeEncrypt("3",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "SABONETE".getBytes(),
                    "Blowfish",
                    null, false);

            question.executeEncrypt("4",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    new byte[]{8,8,8,8,8,8,8,8},
                    "Blowfish",
                    null, false);

            question.executeEncrypt("5",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "SABONETESABONETESABONETE".getBytes(),
                    "Blowfish",
                    null, false);

            question.executeEncrypt("6",
                    Cipher.getInstance("Blowfish/CBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "FURB".getBytes(),
                    "Blowfish",
                    new byte[]{1,1,2,2,3,3,4,4}, false);

            question.executeEncrypt("7",
                    Cipher.getInstance("Blowfish/CBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "SABONETESABONETESABONETE".getBytes(),
                    "Blowfish",
                    new byte[]{1,1,2,2,3,3,4,4}, false);

            question.executeEncrypt("8",
                    Cipher.getInstance("Blowfish/CBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "SABONETESABONETESABONETE".getBytes(),
                    "Blowfish",
                    new byte[]{10,20,30,40,50,60,70,80}, false);

            question.executeDecrypt("8.3",
                    Cipher.getInstance("Blowfish/CBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    new byte[]{16,-104,31,-29,0,-97,31,-32,-85,117,-110,23,-100,54,28,-57,
                            -81,-114,-77,-112,-73,-98,-68,-114,-42,-95,-9,29,67,-31,-64,-60},
                    "Blowfish",
                    new byte[]{1,1,2,2,3,3,4,4}, false);

            try {
                question.executeDecrypt("9",
                        Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                        "11111".getBytes(),
                        new byte[]{127, 71, 0, -86, 111, 95, -32, -117},
                        "Blowfish",
                        null, false);
            } catch (Exception e) {
                System.out.println("Não foi possível criptografar o texto, erro: " + e.getMessage());;
            }

            question.executeEncrypt("10",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    "ABCDE".getBytes(),
                    Files.readAllBytes(Path.of("L0x - Criptografia Blowfish (v6).pdf")),
                    "Blowfish",
                    null, true);

            question.executeDecrypt("11",
                    Cipher.getInstance("Blowfish/ECB/PKCS5Padding"),
                    "ABCDE".getBytes(),
                    Files.readAllBytes(Path.of("saida.bin")),
                    "Blowfish",
                    null, true);

            question.executeEncrypt("12",
                    Cipher.getInstance("Blowfish/PCBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    "FURB".getBytes(),
                    "Blowfish",
                    new byte[]{1,1,2,2,3,3,4,4}, false);

            question.executeEncrypt("13",
                    Cipher.getInstance("Blowfish/CTR/NoPadding"),
                    new byte[]{65,66,67,68,69},
                    "FURB".getBytes(),
                    "Blowfish",
                    new byte[]{0, 20, 10, 120, 0, 0, 0, 0}, false);

            question.executeDecrypt("14",
                    Cipher.getInstance("Blowfish/CBC/PKCS5Padding"),
                    new byte[]{65,66,67,68,69},
                    Files.readAllBytes(Path.of("artigo.bin")),
                    "Blowfish",
                    new byte[]{1,1,1,1,90,90,90,90}, true);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}