package org.aes.Models;

import org.aes.Core.AES;
import org.aes.Services.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
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

        int option;
        try {
            option = sc.nextInt();
            sc.nextLine();
        } catch(InputMismatchException e) {
            System.out.println("\nOpção inválida.");
            sc.nextLine();
            return null;
        }

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
                try {
                    funcOption = sc.nextInt();
                    sc.nextLine();
                } catch(InputMismatchException e) {
                    System.out.println("\nOpção inválida.");
                    sc.nextLine();
                    return null;
                }
                break;
            case 3:
            default:
                System.out.println("\nSaindo...");
                sc.close();
                System.exit(0);
                break;
        }

        if(funcOption == 0) return null;

        File inputFile = null;
        byte[] originalValue = null;
        File outputFile = null;

        if(funcOption == 1) {
            System.out.println("\nInforme o caminho do arquivo de entrada (ou pressione Enter para abrir o seletor): ");
            String inputPath = sc.nextLine().trim();
            if(inputPath.isEmpty()) {
                inputFile = FileService.chooseFile();
            } else {
                inputFile = FileService.getFile(inputPath);
            }
            if(inputFile == null) {
                System.out.println("\nArquivo de entrada não pôde ser carregado.");
                return null;
            }

            System.out.println("\nInforme o nome/caminho do arquivo de saída a ser gerado: ");
            String outputPath = sc.nextLine().trim();
            if(outputPath.isEmpty()) {
                outputFile = FileService.getSaveLocation();
            } else {
                outputFile = new File(outputPath);
            }
            if(outputFile == null) {
                System.out.println("\nOperação cancelada: nenhum arquivo de saída informado.");
                return null;
            }
            if(inputFile.getCanonicalFile().equals(outputFile.getCanonicalFile())) {
                System.out.println("\nO arquivo de saída não pode ser o mesmo da entrada.");
                return null;
            }
        } else if(funcOption == 2) {
            if(option == 2) {
                System.out.println("\nDigite o texto cifrado (hexadecimal): ");
                String fullText = sc.nextLine();
                try {
                    originalValue = AES.fromHexString(fullText);
                } catch(IllegalArgumentException e) {
                    System.out.println("\n" + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("\nDigite o seu texto: ");
                String fullText = sc.nextLine();
                originalValue = fullText.getBytes(StandardCharsets.UTF_8);
            }
        }

        byte[] key = getVector("key", sc);
        if(key == null) return null;

        System.out.println("""
                Qual será o modo de operação a ser utilizado?
                
                1 - ECB (Electronic Codebook)
                2 - CBC (Cipher Block Chaining)
                """);

        int operationMode;
        try {
            operationMode = sc.nextInt();
            sc.nextLine();
        } catch(InputMismatchException e) {
            System.out.println("\nModo de operação inválido.");
            sc.nextLine();
            return null;
        }

        if(operationMode != 1 && operationMode != 2) {
            System.out.println("\nModo de operação inválido.");
            return null;
        }

        byte[] initializationVector = null;
        if(operationMode == 2) {
            initializationVector = getVector("iv", sc);
            if(initializationVector == null) return null;
        }

        CypherData cypherData = new CypherData();
        cypherData.setBytedText(originalValue);
        cypherData.setInputFile(inputFile);
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
            System.out.println("\n" + (type.equals("key") ? "A chave" : "O vetor") + " deve possuir exatamente 16 bytes (128 bits)");
            return null;
        }

        byte[] bytedVector = new byte[16];

        for(int i = 0; i < 16; i++) {
            try {
                int value = Integer.parseInt(stringVector[i].trim());
                if(value < 0 || value > 255) {
                    System.out.println("\nCada byte " + (type.equals("key") ? "da chave" : "do vetor") + " deve estar entre 0 e 255.");
                    return null;
                }
                bytedVector[i] = (byte) value;
            } catch(NumberFormatException e) {
                System.out.println("\nValor numérico inválido: " + stringVector[i]);
                return null;
            }
        }

        return bytedVector;
    }
}
