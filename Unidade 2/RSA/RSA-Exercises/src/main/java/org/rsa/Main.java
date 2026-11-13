package org.rsa;

import org.rsa.Keys.GenerateKeys;
import org.rsa.Keys.GetPrivateKey;
import org.rsa.Keys.GetPublicKey;

import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InvalidKeySpecException {
        try {
            GenerateKeys generator = new GenerateKeys();
            GetPrivateKey getPrivateKey = new GetPrivateKey();
            GetPublicKey getPublicKey = new GetPublicKey();
            generator.generateKeys();
            Cipher c = Cipher.getInstance("RSA");

            Scanner sc = new Scanner(System.in);
            System.out.println("""
                    O que você deseja fazer?
                    
                    1 - Criptografar mensagem (Questão 1)
                    2 - Criptografar pdf da atividade (Questão 2 - ERRO)
                    3 - Criptografar imagem (Questão 3)
                    4 - Assinar arquivo (Questão 4)
                    5 - Descriptografar mensagem (Questão 1)
                    6 - Descriptografar imagem (Questão 3)
                    7 - Validar assinatura digital (Questão 4)
                    
                    """);

            int option = sc.nextInt();

            System.out.println("""
                    Você é a pessoa 1 ou 2?
                    
                    1 - Pessoa 1
                    2 - Pessoa 2
                    
                    """);

            int person = sc.nextInt();

            sc.close();

            switch (option) {
                case 1:
                    if(person == 1) {
                        c.init(Cipher.ENCRYPT_MODE, getPublicKey.getPublicKey(2));
                        byte[] simpleText = "Olá aluno dois, como você está?".getBytes();
                        byte[] encryptedText = c.doFinal(simpleText);

                        Files.write(
                                Path.of("alunoUm/secret_message.txt"),
                                encryptedText
                        );
                    } else {
                        c.init(Cipher.ENCRYPT_MODE, getPublicKey.getPublicKey(1));
                        byte[] simpleText = "Estou bem aluno um, e você?".getBytes();
                        byte[] encryptedText = c.doFinal(simpleText);

                        Files.write(
                                Path.of("alunoDois/secret_message.txt"),
                                encryptedText
                        );
                    }
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    if(person == 1) {
                        c.init(Cipher.DECRYPT_MODE, getPrivateKey.getPrivateKey(1));
                        byte[] encryptedText = Files.readAllBytes(Path.of("alunoDois/secret_message.txt"));
                        byte[] decryptedText = c.doFinal(encryptedText);

                        Files.write(
                                Path.of("alunoUm/mensagem_dois.txt"),
                                decryptedText
                        );
                    } else {
                        c.init(Cipher.DECRYPT_MODE, getPrivateKey.getPrivateKey(2));
                        byte[] encryptedText = Files.readAllBytes(Path.of("alunoUm/secret_message.txt"));
                        byte[] decryptedText = c.doFinal(encryptedText);

                        Files.write(
                                Path.of("alunoDois/mensagem_um.txt"),
                                decryptedText
                        );
                    }
                    break;
                case 6:
                    break;
                case 7:
                    break;
            }

            if(opt == 1) {
                byte[] pdf = Files.readAllBytes(Path.of("alunoUm/ERRO/LE - Chave assimétrica e assinatura.pdf"));
                byte[] encryptedPdf = c.doFinal(pdf);

                Files.write(
                        Path.of("error_pdf.pdf"),
                        encryptedPdf
                );
            }

            //gerando e salvando chaves do enunciado


            //ques


            KeyGenerator keyGenerator;
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey key_aes = keyGenerator.generateKey();

            byte[] imgBytes = Files.readAllBytes(Path.of("img_pessoa_1.png"));
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key_aes);
            byte[] imgFinal = aes.doFinal(imgBytes);
            Files.write(Path.of("img_pessoa_1_criptografado.png"), imgFinal);

        } catch (NoSuchAlgorithmException
                 | IOException
                 | NoSuchPaddingException
                 | InvalidKeyException
                 | IllegalBlockSizeException
                 | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}