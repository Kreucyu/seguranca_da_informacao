package org.aes.Core;

import org.aes.Models.Tables;

public class KeyExpansion {
    public KeyExpansion() {}

    public byte[][][] keyExpansion(byte[][] keyMatrix) {
        byte[][][] roundKeys = new byte[11][4][4];

        if(keyMatrix.length < 4) {
            throw new RuntimeException("\nTamanho de chave inválido");
        }

        for(int i = 0; i < keyMatrix.length; i++) {
            for (int j = 0; j < keyMatrix.length; j++) {
                roundKeys[0][i][j] = keyMatrix[j][i];
            }
        }

        for(int i = 1; i < 11; i++) {
            for(int j = 0; j < 4; j++) {
                if(j == 0) {
                    roundKeys[i][j] = getFirstWord(roundKeys[i-1][3]);
                    continue;
                }
                for(int k = 0; k < 4; k++) {
                    roundKeys[i][j][k] = 0x12;
                }
            }
        }

        return null;
    }

    private byte[] getFirstWord(byte[] lastWord) {
        byte[] rotedWord = rotWord(lastWord);
        byte[] subedWord = subWord(rotedWord);


        return new byte[4];
    }

    private byte[] rotWord(byte[] word) {
        byte[] originalWord = word;

        for(int i = 0; i < word.length; i++) {
            if(i == 3) {
                word[i] = originalWord[0];
                break;
            }
            word[i] = originalWord[i + 1];
        }
        return word;
    }

    private byte[] subWord(byte[] word) {

        for(int i = 0; i < word.length; i++) {
            int row = (word[i] & 0xF0) >> 4;
            int col = word[i] & 0x0F;
            word[i] = (byte) Tables.SBOX[row][col];
        }
        return word;
    }
}
