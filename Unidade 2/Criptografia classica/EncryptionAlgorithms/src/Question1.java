import java.util.Arrays;
import java.util.Scanner;

public class Question1 {
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
        System.out.println("\nDigite o texto que você deseja criptografar: ");
        String toEncrypt = sc.nextLine();
        String toEncryptWithoutSpaces = toEncrypt.replace(" ", "");

        System.out.println("\nDigite a quantidade de colunas que você deseja utilizar: ");
        int columns = sc.nextInt();
        sc.nextLine();

        if(columns < 1) {
            System.out.println("A quantidade de colunas deve ser maior que zero!");
            return;
        }

        int rows = (int) Math.ceil((double) toEncryptWithoutSpaces.length() / columns);
        char[][] matrix = new char[rows][columns];
        int index = 0;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(index < toEncryptWithoutSpaces.length()) {
                    matrix[i][j] = toEncryptWithoutSpaces.charAt(index);
                    index++;
                } else {
                    matrix[i][j] = 'X';
                    if(index < matrix.length) index++;
                }
            }
        }

        StringBuilder finalText = new StringBuilder();
        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {
                finalText.append(matrix[j][i]);
            }
        }
        System.out.println("Matriz: " + Arrays.deepToString(matrix));
        System.out.println("\nO texto criptografado é: " + finalText + "\n");
    }

    public static void decrypt(Scanner sc) {
        System.out.println("\nDigite o texto que você deseja descriptografar: ");
        String toDecrypt = sc.nextLine();

        System.out.println("\nDigite a quantidade de colunas que você deseja utilizar: ");
        int columns = sc.nextInt();
        sc.nextLine();

        if(columns < 1) {
            System.out.println("A quantidade de colunas deve ser maior que zero!");
            return;
        }

        int rows = (int) Math.ceil((double) toDecrypt.length() / columns);
        char[][] matrix = new char[rows][columns];
        int index = 0;

        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {
                matrix[j][i] = toDecrypt.charAt(index);
                index++;
            }
        }

        StringBuilder decryptedText = new StringBuilder();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                decryptedText.append(matrix[i][j]);
            }
        }
        System.out.println("Matriz: " + Arrays.deepToString(matrix));
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
            ============================================================================================
                        Algoritmo de Criptografia - Cifra de Transposição geométrica colunar
            ============================================================================================
            
            O que você deseja fazer?
            
            1 - Criptografar texto
            2 - Descriptografar texto
            3 - Sair
            """);
    }
}
