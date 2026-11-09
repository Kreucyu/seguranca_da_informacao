package org.aes.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataService {
    public DataService() {

    }

    public byte[] applyPadding(byte[] originalText) {
        int paddingLength = 16 - (originalText.length % 16);

        byte[] paddedText = Arrays.copyOf(
                originalText,
                originalText.length + paddingLength
        );

        for (int i = originalText.length; i < paddedText.length; i++) {
            paddedText[i] = (byte) paddingLength;
        }

        return paddedText;
    }

    public byte[] removePadding(byte[] paddedText) {
        if (paddedText.length == 0 || paddedText.length % 16 != 0) {
            throw new IllegalArgumentException("\nTamanho inválido para texto com padding");
        }

        int paddingLength = paddedText[paddedText.length - 1] & 0xFF;

        if (paddingLength < 1 || paddingLength > 16) {
            throw new IllegalArgumentException("Padding inválido");
        }

        for (int i = paddedText.length - paddingLength; i < paddedText.length; i++) {
            if ((paddedText[i] & 0xFF) != paddingLength) {
                throw new IllegalArgumentException("\nPadding inválido");
            }
        }
        return Arrays.copyOf(paddedText, paddedText.length - paddingLength);
    }

    public List<byte[][]> getWordsMatrix(byte[] paddedText) {
        List<byte[][]> allWords = new ArrayList<>();
        int quantity = paddedText.length / 16;

        for(int block = 0; block < quantity; block++) {
            byte[][] partialWord = new byte[4][4];

            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    int actualWord = (block * 16) + (j * 4) + i;
                    partialWord[i][j] = paddedText[actualWord];
                }
            }
            allWords.add(partialWord);
        }
        return allWords;
    }
}
