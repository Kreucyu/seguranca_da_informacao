package org.aes;

import org.aes.Core.AES;
import org.aes.Models.CypherData;
import org.aes.Models.Menu;
import org.aes.Ui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            runCli();
            return;
        }

        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow window = new MainWindow();
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        runCli();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            runCli();
        }
    }

    public static void runCli() throws IOException {
        Scanner sc = new Scanner(System.in);
        CypherData cypherData;
        AES aes;
        while (true) {
            cypherData = Menu.loadMenuAndGetValues(sc);
            if (cypherData == null) continue;
            aes = new AES();
            try {
                aes.start(cypherData);
            } catch (Exception e) {
                System.out.println("\nErro: " + e.getMessage());
            }
        }
    }
}
