package org.rsa;

import org.rsa.Keys.GenerateKeys;
import org.rsa.Keys.GetPrivateKey;
import org.rsa.Keys.GetPublicKey;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            GenerateKeys generator = new GenerateKeys();
            GetPrivateKey getPrivateKey = new GetPrivateKey();
            GetPublicKey getPublicKey = new GetPublicKey();

            if (!generator.keysExist()) {
                generator.generateKeys();
            }

            Cipher rsaCipher = Cipher.getInstance("RSA");
            Scanner sc = new Scanner(System.in);

            System.out.println("""
                    Exercícios RSA

                    0 - Gerar / Regenerar chaves RSA (Alunos 1 e 2)
                    1 - Criptografar mensagem curta (Questão 1)
                    2 - Criptografar PDF da atividade (Questão 2 - ERRO)
                    3 - Criptografar imagem com Envelope Digital (Questão 3)
                    4 - Assinar arquivo digitalmente (Questão 4)
                    5 - Descriptografar mensagem (Questão 1)
                    6 - Descriptografar imagem abrindo Envelope Digital (Questão 3)
                    7 - Validar assinatura digital (Questão 4)

                    Escolha uma opção:
                    """);

            int option = sc.nextInt();
            sc.nextLine();

            if (option == 0) {
                generator.generateKeys();
                System.out.println("\nChaves RSA geradas e salvas com sucesso em alunoUm/keys e alunoDois/keys!\n");
                return;
            }

            System.out.println("""
                    \nVocê é a pessoa 1 ou 2?
                    1 - Aluno 1
                    2 - Aluno 2
                    """);
            int person = sc.nextInt();
            sc.nextLine();

            int otherPerson = (person == 1) ? 2 : 1;
            String myDir = (person == 1) ? "alunoUm" : "alunoDois";
            String otherDir = (person == 1) ? "alunoDois" : "alunoUm";

            switch (option) {
                case 1: {
                    System.out.println("\nQUESTÃO 1: Criptografar mensagem curta\n");
                    String defaultMsg = (person == 1)
                            ? "Olá aluno dois, como você está?"
                            : "Estou bem aluno um, e você?";

                    rsaCipher.init(Cipher.ENCRYPT_MODE, getPublicKey.getPublicKey(otherPerson));
                    byte[] encryptedText = rsaCipher.doFinal(defaultMsg.getBytes(StandardCharsets.UTF_8));

                    Path outMsgPath = Path.of(myDir, "secret_message.txt");
                    Files.write(outMsgPath, encryptedText);

                    System.out.println("Mensagem cifrada com sucesso para o Aluno " + otherPerson + "!");
                    System.out.println("Arquivo salvo em: " + outMsgPath + "\n");
                    break;
                }

                case 2: {
                    System.out.println("\nQUESTÃO 2: Criptografar arquivo PDF da atividade\n");
                    tentarCriptografarPdf(rsaCipher, getPublicKey.getPublicKey(otherPerson));
                    break;
                }

                case 3: {
                    System.out.println("\nQUESTÃO 3: Criptografar imagem com AES e Envelope Digital\n");
                    String imgFileName = (person == 1) ? "img_pessoa_1.png" : "img_pessoa_2.png";
                    Path imgPath = Path.of(myDir, imgFileName);

                    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                    keyGen.init(128);
                    SecretKey aesKey = keyGen.generateKey();

                    Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    byte[] imgBytes = Files.readAllBytes(imgPath);
                    byte[] encryptedImg = aesCipher.doFinal(imgBytes);

                    Path encryptedImgPath = Path.of(myDir, "imagem_cifrada.enc");
                    Files.write(encryptedImgPath, encryptedImg);

                    rsaCipher.init(Cipher.ENCRYPT_MODE, getPublicKey.getPublicKey(otherPerson));
                    byte[] digitalEnvelope = rsaCipher.doFinal(aesKey.getEncoded());

                    Path envelopePath = Path.of(myDir, "envelope_digital.bin");
                    Files.write(envelopePath, digitalEnvelope);

                    System.out.println("1. Imagem cifrada com AES em: " + encryptedImgPath);
                    System.out.println("2. Envelope digital gerado em: " + envelopePath);
                    System.out.println("\nOperação concluída com sucesso!\n");
                    break;
                }

                case 4: {
                    System.out.println("\nQUESTÃO 4: Assinar arquivo digitalmente\n");
                    Path docPath = Path.of(myDir, "documento.txt");

                    Signature signature = Signature.getInstance("SHA256withRSA");
                    signature.initSign(getPrivateKey.getPrivateKey(person));
                    byte[] docBytes = Files.readAllBytes(docPath);
                    signature.update(docBytes);
                    byte[] sigBytes = signature.sign();

                    Path sigPath = Path.of(myDir, "documento.sig");
                    Files.write(sigPath, sigBytes);

                    System.out.println("Documento assinado com a chave PRIVADA do Aluno " + person + "!");
                    System.out.println("Documento: " + docPath);
                    System.out.println("Assinatura gerada em: " + sigPath + "\n");
                    break;
                }

                case 5: {
                    System.out.println("\nQUESTÃO 1: Descriptografar mensagem curta recebida\n");
                    Path receivedMsgPath = Path.of(otherDir, "secret_message.txt");

                    rsaCipher.init(Cipher.DECRYPT_MODE, getPrivateKey.getPrivateKey(person));
                    byte[] encryptedBytes = Files.readAllBytes(receivedMsgPath);
                    byte[] decryptedBytes = rsaCipher.doFinal(encryptedBytes);
                    String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

                    Path outDecryptedPath = Path.of(myDir, "mensagem_recebida_de_aluno" + otherPerson + ".txt");
                    Files.writeString(outDecryptedPath, decryptedText, StandardCharsets.UTF_8);

                    System.out.println("Mensagem decifrada com sucesso pelo Aluno " + person + ": " + decryptedText);
                    System.out.println("Texto salvo em: " + outDecryptedPath + "\n");
                    break;
                }

                case 6: {
                    System.out.println("\nQUESTÃO 3: Abrir Envelope Digital e Decifrar Imagem\n");
                    Path receivedEnvelope = Path.of(otherDir, "envelope_digital.bin");
                    Path receivedEncryptedImg = Path.of(otherDir, "imagem_cifrada.enc");

                    rsaCipher.init(Cipher.DECRYPT_MODE, getPrivateKey.getPrivateKey(person));
                    byte[] envelopeBytes = Files.readAllBytes(receivedEnvelope);
                    byte[] aesKeyBytes = rsaCipher.doFinal(envelopeBytes);
                    SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                    Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] encryptedImgBytes = Files.readAllBytes(receivedEncryptedImg);
                    byte[] decryptedImgBytes = aesCipher.doFinal(encryptedImgBytes);

                    String restoredFileName = (person == 1) ? "img_pessoa_2_descriptografada.png"
                            : "img_pessoa_1_descriptografada.png";
                    Path decryptedImgPath = Path.of(myDir, restoredFileName);
                    Files.write(decryptedImgPath, decryptedImgBytes);

                    System.out.println("Envelope digital aberto e imagem decifrada com sucesso!");
                    System.out.println("Imagem salva em: " + decryptedImgPath + "\n");
                    break;
                }

                case 7: {
                    System.out.println("\nQUESTÃO 4: Validar assinatura digital\n");
                    Path receivedDoc = Path.of(otherDir, "documento.txt");
                    Path receivedSig = Path.of(otherDir, "documento.sig");

                    Signature signature = Signature.getInstance("SHA256withRSA");
                    signature.initVerify(getPublicKey.getPublicKey(otherPerson));
                    byte[] docBytes = Files.readAllBytes(receivedDoc);
                    signature.update(docBytes);
                    byte[] sigBytes = Files.readAllBytes(receivedSig);

                    boolean isValid = signature.verify(sigBytes);

                    System.out.println("Resultado da validação da assinatura do Aluno " + otherPerson + ":");
                    if (isValid) {
                        System.out.println("\nASSINATURA VÁLIDA\n");
                    } else {
                        System.out.println("\nASSINATURA INVÁLIDA\n");
                    }
                    break;
                }

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tentarCriptografarPdf(Cipher rsaCipher, PublicKey recipientPublicKey) {
        Path pdfPath = Path.of("LE - Chave assimétrica e assinatura.pdf");

        try {
            System.out.println("Tentando criptografar o arquivo: " + pdfPath.getFileName());
            System.out.println("Tamanho do arquivo: " + Files.size(pdfPath) + " bytes.");

            rsaCipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey);
            byte[] pdfBytes = Files.readAllBytes(pdfPath);
            rsaCipher.doFinal(pdfBytes);

            System.out.println("Arquivo criptografado com sucesso.");
        } catch (IllegalBlockSizeException e) {
            System.out.println("Exceção lançada: " + e.getClass().getName());
            System.out.println("Mensagem: " + e.getMessage());
            System.out.println(
                    "\nO arquivo PDF é muito maior que o limite suportado por um bloco RSA (117 bytes com chave de 1024 bits e padding PKCS#1).\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
