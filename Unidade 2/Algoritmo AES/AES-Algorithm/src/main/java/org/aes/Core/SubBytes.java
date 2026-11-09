package org.aes.Core;

import org.aes.Models.Tables;

public class SubBytes {
    public SubBytes() {}

    public byte[][] subBytes(byte[][] word) {
        for(int i = 0; i < word.length; i++) {
            for(int j = 0; j < word.length; j++) {
                int row = (word[i][j] & 0xF0) >> 4;
                int col = word[i][j] & 0x0F;
                word[i][j] = (byte) Tables.SBOX[row][col];
            }
        }
        return word;
    }

    public void invSubBytes() {}
}
