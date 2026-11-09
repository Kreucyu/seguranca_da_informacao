package org.aes.Core;

import org.aes.Models.Tables;

import java.util.Map;

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
                    roundKeys[i][j] = getFirstWord(roundKeys[i-1][3], i, roundKeys[i-1][0]);
                    continue;
                }
                roundKeys[i][j] = doXOR(roundKeys[i-1][j], roundKeys[i][j-1]);
            }
        }
        return roundKeys;
    }

    private byte[] getFirstWord(byte[] lastWord, int actualRoundKey, byte[] lastFirstWord) {
        byte[] rotedWord = rotWord(lastWord);
        byte[] subbedWord = subWord(rotedWord);
        byte[] roundConstant = getRoundConstant(actualRoundKey);
        byte[] xorWordFirst = doXOR(subbedWord, roundConstant);
        return doXOR(lastFirstWord, xorWordFirst);
    }

    private byte[] rotWord(byte[] word) {
        byte[] originalWord = word.clone();

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

    private byte[] getRoundConstant(int actualRoundKey) {
        Map<Integer,Byte> getConstant = Map.of(
                1, (byte) 0x01,
                2, (byte) 0x02,
                3, (byte) 0x04,
                4, (byte) 0x08,
                5, (byte) 0x010,
                6, (byte) 0x20,
                7, (byte) 0x40,
                8, (byte) 0x80,
                9, (byte) 0x1B,
                10, (byte) 0x36
        );
        return new byte[]{getConstant.get(actualRoundKey), 0x00, 0x00, 0x00};
    }

    private byte[] doXOR(byte[] firstWord, byte[] secondWord) {
        byte[] xoredWord = new byte[4];
        for(int i = 0; i < 4; i++) {
            xoredWord[i] = (byte) (firstWord[i] ^ secondWord[i]);
        }
        return xoredWord;
    }
}
