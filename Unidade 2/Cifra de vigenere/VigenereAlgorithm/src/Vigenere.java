import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Vigenere {

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int option = 0;

        while(true) {
            showMenu();
            option = sc.nextInt();
            sc.nextLine();

            if(option < 0 || option > 3) {
                System.out.println("Escolha um valor válido!");
                clear(6000);
                continue;
            }

            if(option == 3) {
                System.out.println("Saindo...");
                clear(3000);
                System.exit(0);
            }

            if(option == 1) {
                encrypt(sc);
                clear(10000);
                continue;
            }

            decrypt(sc);
            clear(15000);
        }
    }

    public static void encrypt(Scanner sc) {
        String alphabet = getAlphabet();
        System.out.println("\nDigite o texto que você deseja criptografar: ");
        String toEncrypt = sc.nextLine();

        if(toEncrypt.isEmpty()) {
            System.out.println("Digite um texto válido!");
            return;
        }

        System.out.println("""
        
        Deseja remover os espaços do texto?
        
        1 - Sim
        2 - Não
        """);
        int option = sc.nextInt();
        sc.nextLine();

        if(option == 1) {
            toEncrypt = toEncrypt.replace(" ", "");
        }

        String toEncryptUpperCase = toEncrypt.toUpperCase();

        System.out.println("\nDigite a chave que você deseja utilizar: ");
        String key = sc.nextLine();
        key = key.replace(" ", "");
        String keyUpperCase = key.toUpperCase();

        String compareSize = toEncrypt.replace(" ", "");
        if(key.length() > compareSize.length()) {
            System.out.println("A chave deve ser menor que o tamanho do texto a ser criptografado!");
            return;
        }

        if(keyUpperCase.isEmpty()) {
            System.out.println("Digite uma chave válida!");
            return;
        }

        StringBuilder finalText = new StringBuilder();

        int j = 0;
        for(int i = 0; i < toEncryptUpperCase.length(); i++) {
            if(toEncryptUpperCase.charAt(i) == ' ') {
                finalText.append(" ");
                continue;
            }
            if(j >= keyUpperCase.length()) j = 0;

            char actualLetter = toEncryptUpperCase.charAt(i);
            int originalPosition = alphabet.indexOf(actualLetter);

            char actualKeyLetter = keyUpperCase.charAt(j);
            int keyPosition = alphabet.indexOf(actualKeyLetter);

            int newPosition = originalPosition + keyPosition;
            if(newPosition > 25) newPosition = newPosition - 25;

            char newLetter = alphabet.charAt(newPosition);
            finalText.append(newLetter);
            j++;
        }
        System.out.println("\nO texto criptografado é: " + finalText + "\n");
    }

    public static void decrypt(Scanner sc) {
        String alphabet = getAlphabet();
        System.out.println("\nDigite o texto que você deseja descriptografar: ");
        String toDecrypt = sc.nextLine();

        if(toDecrypt.isEmpty()) {
            System.out.println("Digite um texto válido!");
            return;
        }

        System.out.println("""
        
        Deseja remover os espaços do texto?
        
        1 - Sim
        2 - Não
        """);
        int option = sc.nextInt();
        sc.nextLine();

        if(option == 1) {
            toDecrypt = toDecrypt.replace(" ", "");
        }

        String toDecryptUpperCase = toDecrypt.toUpperCase();

        System.out.println("\nDigite a chave que você deseja utilizar: ");
        String key = sc.nextLine();
        key = key.replace(" ", "");
        String keyUpperCase = key.toUpperCase();
        String compareSize = toDecrypt.replace(" ", "");

        if(key.length() > compareSize.length()) {
            System.out.println("A chave deve ser menor que o tamanho do texto a ser criptografado!");
            return;
        }

        if(key.isEmpty()) {
            System.out.println("Digite uma chave válida!");
            return;
        }

        StringBuilder decryptedText = new StringBuilder();
        int j = 0;
        for(int i = 0; i < toDecryptUpperCase.length(); i++) {
            if(toDecrypt.charAt(i) == ' ') {
                decryptedText.append(" ");
                continue;
            }
            if(j >= keyUpperCase.length()) j = 0;

            char actualLetter = toDecrypt.charAt(i);
            int originalPosition = alphabet.indexOf(actualLetter);

            char actualKeyLetter = keyUpperCase.charAt(j);
            int keyPosition = alphabet.indexOf(actualKeyLetter);

            int newPosition = originalPosition - keyPosition;
            if(newPosition < 0) newPosition =  25 - (newPosition * -1);

            char newLetter = alphabet.charAt(newPosition);
            decryptedText.append(newLetter);
            j++;
        }
        System.out.println("\nO texto descriptografado é: " + decryptedText + "\n");

    }

    public static void clear(Integer milis) throws InterruptedException {
        Thread.sleep(milis);
        for(int i = 0; i < 50; i++) {

            System.out.println();
        }
    }

    public static void showMenu() {
        System.out.println("""
            =====================================================================
                        Algoritmo de Criptografia - Cifra de Vigenere
            =====================================================================
            
            O que você deseja fazer?
            
            1 - Criptografar texto
            2 - Descriptografar texto
            3 - Sair
            """);
    }

    public static String getAlphabet() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }
}
