package org.aes.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataService {
    public DataService() {

    }

    public byte[][] bytesToBlock(byte[] raw16) {
        byte[][] block = new byte[4][4];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                block[i][j] = raw16[j * 4 + i];
            }
        }
        return block;
    }

    public byte[] blockToBytes(byte[][] block) {
        byte[] raw = new byte[16];
        for(int col = 0; col < 4; col++) {
            for(int row = 0; row < 4; row++) {
                raw[col * 4 + row] = block[row][col];
            }
        }
        return raw;
    }

    public byte[] applyPaddingToBlock(byte[] partial, int read) {
        int paddingLength = 16 - read;
        byte[] block = new byte[16];
        System.arraycopy(partial, 0, block, 0, read);
        for(int i = read; i < 16; i++) {
            block[i] = (byte) paddingLength;
        }
        return block;
    }

    public byte[] removePaddingFromBlock(byte[] raw16) {
        int paddingLength = raw16[15] & 0xFF;
        if(paddingLength < 1 || paddingLength > 16) {
            throw new IllegalArgumentException("\nPadding inválido");
        }
        for(int i = 16 - paddingLength; i < 16; i++) {
            if((raw16[i] & 0xFF) != paddingLength) {
                throw new IllegalArgumentException("\nPadding inválido");
            }
        }
        byte[] result = new byte[16 - paddingLength];
        System.arraycopy(raw16, 0, result, 0, result.length);
        return result;
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
            throw new IllegalArgumentException("\nPadding inválido");
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
