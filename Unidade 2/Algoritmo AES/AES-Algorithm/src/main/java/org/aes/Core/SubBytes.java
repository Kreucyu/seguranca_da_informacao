package org.aes.Core;

import org.aes.Models.Tables;

public class SubBytes {
    public SubBytes() {}

    public byte[][] subBytes(byte[][] wordMatrix) {
        for(int i = 0; i < wordMatrix.length; i++) {
            for(int j = 0; j < wordMatrix.length; j++) {
                int row = (wordMatrix[i][j] & 0xF0) >> 4;
                int col = wordMatrix[i][j] & 0x0F;
                wordMatrix[i][j] = (byte) Tables.SBOX[row][col];
            }
        }
        return wordMatrix;
    }

    public byte[][] invSubBytes(byte[][] wordMatrix) {
        for(int i = 0; i < wordMatrix.length; i++) {
            for(int j = 0; j < wordMatrix.length; j++) {
                int row = (wordMatrix[i][j] & 0xF0) >> 4;
                int col = wordMatrix[i][j] & 0x0F;
                wordMatrix[i][j] = (byte) Tables.INVERTED_SBOX[row][col];
            }
        }
        return wordMatrix;
    }
}
