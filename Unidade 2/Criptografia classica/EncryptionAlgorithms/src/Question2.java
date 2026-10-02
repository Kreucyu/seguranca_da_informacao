import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Question2 {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int option = 0;

        while(true) {
            showMenu();
            option = sc.nextInt();
            sc.nextLine();

            if(option < 0 || option > 3) {
                System.out.println("Escolha um valor válido!");
                Question1.clear(6000);
                continue;
            }

            if(option == 3) {
                System.out.println("Saindo...");
                Question1.clear(3000);
                System.exit(0);
            }

            if(option == 1) {
                encrypt(sc);
                Question1.clear(10000);
                continue;
            }

            decrypt(sc);
            Question1.clear(15000);
        }
    }

    public static void encrypt(Scanner sc) {
        System.out.println("\nDigite o texto que você deseja criptografar: ");
        String toEncrypt = sc.nextLine();
        String toEncryptWithoutSpaces = toEncrypt.replace(" ", "");

        System.out.println("\nDigite a quantidade de trilhos que você deseja utilizar: ");
        int rails = sc.nextInt();
        sc.nextLine();

        if(rails < 1) {
            System.out.println("A quantidade de trilhos deve ser maior que zero!");
            return;
        }

        StringBuilder[] allRails = new StringBuilder[rails];
        for(int i = 0; i < allRails.length; i++) {
            allRails[i] = new StringBuilder();
        }

        int[] sequence = getSequence(toEncryptWithoutSpaces.length(), rails);

        for(int i = 0; i < toEncryptWithoutSpaces.length(); i++) {
            allRails[sequence[i]].append(toEncryptWithoutSpaces.charAt(i));
        }

        StringBuilder finalText = new StringBuilder();
        for (StringBuilder allRail : allRails) {
            finalText.append(allRail.toString());
        }
        System.out.println("\nO texto criptografado é: " + finalText + "\n");
    }

    public static void decrypt(Scanner sc) {
        System.out.println("\nDigite o texto que você deseja descriptografar: ");
        String toDecrypt = sc.nextLine();

        System.out.println("\nDigite a quantidade de trilhos que você deseja utilizar: ");
        int rails = sc.nextInt();
        sc.nextLine();

        if (rails < 1) {
            System.out.println("A quantidade de trilhos deve ser maior que zero!");
            return;
        }

        List<char[]> allRails = new ArrayList<>();
        for (int i = 0; i < rails; i++) {
            allRails.add(new char[toDecrypt.length()]);
        }

        int[] sequence = getSequence(toDecrypt.length(), rails);

        for (int i = 0; i < toDecrypt.length(); i++) {
            allRails.get(sequence[i])[i] = '*';
        }

        System.out.println("\nMapeamento: \n");
        for (char[] chars : allRails) {
            System.out.println(Arrays.toString(chars));
        }

        int actualChar = 0;

        for (char[] chars : allRails) {
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == '*') {
                    chars[j] = toDecrypt.charAt(actualChar);
                    actualChar++;
                }
            }
        }

        System.out.println("\nSubstituição: \n");
        for(char[] chars : allRails) {
            System.out.println(Arrays.toString(chars));
        }

        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < toDecrypt.length(); i++) {
            decryptedText.append(allRails.get(sequence[i])[i]);
        }
        System.out.println("\nO texto descriptografado é: " + decryptedText + "\n");
    }

    private static int[] getSequence(int length, int rails) {
        int[] sequence = new int[length];
        int actualRail = 0;
        int direction = 1;

        for(int i = 0; i < length; i++) {
            sequence[i] = actualRail;
            if (actualRail == 0) {
                direction = 1;
            } else if (actualRail == rails - 1) {
                direction = -1;
            }
            actualRail += direction;
        }

        return sequence;
    }

    public static void showMenu() {
        System.out.println("""
            =====================================================================
                        Algoritmo de Criptografia - Cerca ferroviária
            =====================================================================
            
            O que você deseja fazer?
            
            1 - Criptografar texto
            2 - Descriptografar texto
            3 - Sair
            """);
    }
}
