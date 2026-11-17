package org.aes.Core;

import org.aes.Models.Tables;

public class KeyExpansion {

    private static final byte[] RCON = {
        0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte)0x80, 0x1B, 0x36
    };

    public KeyExpansion() {}

    public byte[][][] keyExpansion(byte[][] keyMatrix) {
        byte[][][] roundKeys = new byte[11][4][4];
        byte[][] words = new byte[44][4];

        for(int j = 0; j < 4; j++) {
            for(int i = 0; i < 4; i++) {
                words[j][i] = keyMatrix[i][j];
            }
        }

        for(int i = 4; i < 44; i++) {
            byte[] temp = words[i - 1].clone();
            if(i % 4 == 0) {
                temp = getFirstWord(temp, i / 4);
            }
            words[i] = doXOR(words[i - 4], temp);
        }

        for(int r = 0; r < 11; r++) {
            for(int col = 0; col < 4; col++) {
                for(int row = 0; row < 4; row++) {
                    roundKeys[r][row][col] = words[r * 4 + col][row];
                }
            }
        }
        return roundKeys;
    }

    private byte[] getFirstWord(byte[] lastWord, int actualRoundKey) {
        byte[] rotedWord = rotWord(lastWord);
        byte[] subbedWord = subWord(rotedWord);
        byte[] roundConstant = getRoundConstant(actualRoundKey);
        return doXOR(subbedWord, roundConstant);
    }

    private byte[] rotWord(byte[] word) {
        byte[] roted = new byte[4];
        roted[0] = word[1];
        roted[1] = word[2];
        roted[2] = word[3];
        roted[3] = word[0];
        return roted;
    }

    private byte[] subWord(byte[] word) {
        byte[] subbed = new byte[4];
        for(int i = 0; i < word.length; i++) {
            int row = (word[i] & 0xF0) >> 4;
            int col = word[i] & 0x0F;
            subbed[i] = (byte) Tables.SBOX[row][col];
        }
        return subbed;
    }

    private byte[] getRoundConstant(int actualRoundKey) {
        return new byte[]{RCON[actualRoundKey], 0x00, 0x00, 0x00};
    }

    private byte[] doXOR(byte[] firstWord, byte[] secondWord) {
        byte[] xoredWord = new byte[4];
        for(int i = 0; i < 4; i++) {
            xoredWord[i] = (byte) (firstWord[i] ^ secondWord[i]);
        }
        return xoredWord;
    }
}
