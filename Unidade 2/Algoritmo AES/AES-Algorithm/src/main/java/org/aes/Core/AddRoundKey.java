package org.aes.Core;

import org.aes.Models.CypherData;

public class AddRoundKey {
    public AddRoundKey() {}

    public byte[][] addRoundkey(byte[][] word, byte[][] roundKey) {
        byte[][] roundedWord = new byte[4][4];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                roundedWord[i][j] = (byte) (word[i][j] ^ roundKey[i][j]);
            }
        }
        return roundedWord;
    }
}
