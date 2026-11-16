package org.aes.Models;

import org.aes.Services.FileService;

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
                        
                        1 - Arquivo (qualquer tipo / binário)
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
        File outputFile = null;

        if(funcOption == 1) {
            System.out.println("\nInforme o caminho do arquivo de entrada (ou pressione Enter para abrir o seletor): ");
            String inputPath = sc.nextLine().trim();
            if(inputPath.isEmpty()) {
                originalValue = FileService.chooseFile();
            } else {
                originalValue = FileService.readFile(inputPath);
            }
            if(originalValue == null) {
                System.out.println("Arquivo de entrada não pôde ser carregado.");
                return null;
            }

            System.out.println("\nInforme o nome/caminho do arquivo de saída a ser gerado: ");
            String outputPath = sc.nextLine().trim();
            if(outputPath.isEmpty()) {
                outputFile = FileService.getSaveLocation();
            } else {
                outputFile = new File(outputPath);
            }
        } else if (funcOption == 2) {
            System.out.println("\nDigite o seu texto: ");
            String fullText = sc.nextLine();
            originalValue = fullText.getBytes(StandardCharsets.UTF_8);
        }

        byte[] key = getVector("key", sc);
        if(key == null) return null;

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
            if(initializationVector == null) return null;
        }

        CypherData cypherData = new CypherData();

        cypherData.setBytedText(originalValue);
        cypherData.setKey(key);
        cypherData.setInitializationVector(initializationVector);
        cypherData.setFinalObjectType(funcOption == 1 ? "file" : "text");
        cypherData.setOutputFile(outputFile);
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
            try {
                int value = Integer.parseInt(stringVector[i].trim());
                if (value < 0 || value > 255) {
                    System.out.println("\nCada byte "  + (type.equals("key") ? "da chave" : "do vetor") + " deve estar entre 0 e 255.");
                    return null;
                }
                bytedVector[i] = (byte) value;
            } catch (NumberFormatException e) {
                System.out.println("\nValor numérico inválido: " + stringVector[i]);
                return null;
            }
        }

        return bytedVector;
    }
}
