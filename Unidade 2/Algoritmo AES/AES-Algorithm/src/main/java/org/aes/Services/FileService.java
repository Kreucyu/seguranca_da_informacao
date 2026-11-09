package org.aes.Services;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileService {

    public FileService() {}

    public static byte[] chooseFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione um arquivo");

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            System.out.println("\nSelecionado: " + file.getAbsolutePath());
             return Files.readAllBytes(file.toPath());
        } else {
            System.out.println("\nNenhum arquivo selecionado.");
            return null;
        }
    }

    public static File getSaveLocation() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Salvar arquivo criptografado");

        fileChooser.setSelectedFile(
                new File("arquivo.enc")
        );

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }
}
