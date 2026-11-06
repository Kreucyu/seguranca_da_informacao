package org.aes.Models;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public class Menu {
    public Menu() {}

    public static CypherData loadMenuAndGetValues(Scanner sc) throws IOException {
        System.out.println("""
                =============================> Algoritmo - AES <=============================
                
                O que você deseja fazer?
                
                1 - Criptografar
                2 - Descriptografar
                3 - Sair
                """);

        int option = sc.nextInt();
        sc.nextLine();
        String function = option == 1 ? "criptografar?" : "descriptografar?";
        int funcOption = 0;

        switch(option) {
            case 1:
            case 2:
                System.out.println("\nO que você deseja " + function);
                System.out.println("""
                        
                        1 - Arquivo de texto (externo)
                        2 - Texto (escrito)
                        """);
                funcOption = sc.nextInt();
                sc.nextLine();
                break;
            case 3:
            default:
                System.out.println("\nSaindo...");
                sc.close();
                System.exit(0);
                break;
        }

        if(funcOption == 0) return null;

        byte[] originalValue = null;

        if(funcOption == 1) {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Selecione um arquivo");

            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                System.out.println("\nSelecionado: " + file.getAbsolutePath());
                originalValue = Files.readAllBytes(file.toPath());
            } else {
                System.out.println("\nNenhum arquivo selecionado.");
                return null;
            }

        } else if (funcOption == 2) {
            System.out.println("\nDigite o seu texto: ");
            String fullText = sc.nextLine();
            originalValue = fullText.getBytes(StandardCharsets.UTF_8);
        }

        byte[] key = getVector("key", sc);

        System.out.println("""
                Qual será o modo de operação a ser utilizado?
                
                1 - ECB (Electronic Codebook)
                2 - CBC (Cipher Block Chaining)
                """);

        int operationMode = sc.nextInt();
        sc.nextLine();
        byte[] initializationVector = null;

        if(operationMode == 2) {
            initializationVector = getVector("iv", sc);
        }

        CypherData cypherData = new CypherData();

        cypherData.setBytedText(originalValue);
        cypherData.setKey(key);
        cypherData.setInitializationVector(initializationVector);
        cypherData.setFinalObjectType(funcOption == 1 ? "file" : "text");
        cypherData.setOperation(option == 1 ? "encrypt" : "decrypt");
        cypherData.setOperationMode(operationMode == 1 ? "ECB" : "CBC");

        return cypherData;
    }

    private static byte[] getVector(String type, Scanner sc) {
        System.out.println("\nInsira " + (type.equals("key") ? "a chave" : "o vetor") + " que deseja utilizar:");
        String rawText = sc.nextLine();
        String[] stringVector = rawText.split(",");

        if(stringVector.length != 16) {
            System.out.println("\n" + (type.equals("key") ? "A chave" : "O vetor") + " deve possuir extamente 16 bytes (128 bits)");
            return null;
        }

        byte[] bytedVector = new byte[16];

        for (int i = 0; i < 16; i++) {
            int value = Integer.parseInt(stringVector[i].trim());
            if (value < 0 || value > 255) {
                System.out.println("\nCada byte"  + (type.equals("key") ? "da chave" : "do vetor") + " deve estar entre 0 e 255.");
                return null;
            }
            bytedVector[i] = (byte) value;
        }

        return bytedVector;
    }
}
