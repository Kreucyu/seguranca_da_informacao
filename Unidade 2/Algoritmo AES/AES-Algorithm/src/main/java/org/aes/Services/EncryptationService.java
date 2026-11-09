package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;
import org.aes.Models.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncryptationService {

    private final AddRoundKey addRoundKey;
    private final KeyExpansion keyExpansion;
    private final MixColumns mixColumns;
    private final ShiftRows shiftRows;
    private final SubBytes subBytes;
    private final DataService dataService;

    public EncryptationService() {
        this.addRoundKey = new AddRoundKey();
        this.keyExpansion = new KeyExpansion();
        this.mixColumns = new MixColumns();
        this.shiftRows = new ShiftRows();
        this.subBytes = new SubBytes();
        this.dataService = new DataService();
    }

    public byte[] encrypt(CypherData data) {
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        byte[] paddedText = dataService.applyPadding(data.getBytedText());
        List<byte[][]> allWords = dataService.getWordsMatrix(paddedText);

        for(int i = 0; i < allWords.size(); i++) {
            byte[][] actualWord = allWords.get(i);
            actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[0]);

            for(int j = 1; j < roundKeys.length; j++) {
                actualWord = subBytes.subBytes(actualWord);
                actualWord = shiftRows.shiftRows(actualWord);
                if(j != 10) {
                    actualWord = mixColumns.mixColumns(actualWord);
                }
                actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[j]);
            }

            allWords.set(i, actualWord);
        }
        return getEncryptedBytes(allWords);
    }

    private byte[] getEncryptedBytes(List<byte[][]> allWords) {
        byte[] encryptedText = new byte[allWords.size() * 16];
        int index = 0;

        for (byte[][] word : allWords) {
            for (int column = 0; column < 4; column++) {
                for (int row = 0; row < 4; row++) {
                    encryptedText[index++] = word[row][column];
                }
            }
        }
        return encryptedText;
    }
}
