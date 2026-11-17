package org.aes.Services;

import javax.swing.*;
import java.io.File;

public class FileService {

    public FileService() {}

    public static File getFile(String path) {
        File file = new File(path);
        if(!file.exists()) {
            System.out.println("\nArquivo não encontrado: " + path);
            return null;
        }
        System.out.println("\nArquivo selecionado: " + file.getAbsolutePath());
        return file;
    }

    public static File chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione um arquivo");
        int result = chooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            System.out.println("\nSelecionado: " + file.getAbsolutePath());
            return file;
        } else {
            System.out.println("\nNenhum arquivo selecionado.");
            return null;
        }
    }

    public static File getSaveLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo");
        fileChooser.setSelectedFile(new File("arquivo.enc"));
        int result = fileChooser.showSaveDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
