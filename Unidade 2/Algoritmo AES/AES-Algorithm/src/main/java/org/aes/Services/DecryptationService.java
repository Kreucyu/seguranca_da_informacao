package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;
import org.aes.Models.State;

import java.util.List;

public class DecryptationService {
    private final AddRoundKey addRoundKey;
    private final KeyExpansion keyExpansion;
    private final MixColumns mixColumns;
    private final ShiftRows shiftRows;
    private final SubBytes subBytes;
    private final DataService dataService;

    public DecryptationService() {
        this.addRoundKey = new AddRoundKey();
        this.keyExpansion = new KeyExpansion();
        this.mixColumns = new MixColumns();
        this.shiftRows = new ShiftRows();
        this.subBytes = new SubBytes();
        this.dataService = new DataService();
    }

    public byte[] decrypt(CypherData data) {
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        List<byte[][]> allWords = dataService.getWordsMatrix(data.getBytedText());

        for(int i = 0; i < allWords.size(); i++) {
            byte[][] actualWord = allWords.get(i);
            actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[10]);

            for(int j = 9; j >= 1; j--) {
                actualWord = shiftRows.invShiftRows(actualWord);
                actualWord = subBytes.invSubBytes(actualWord);
                actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[j]);
                actualWord = mixColumns.invMixColumns(actualWord);
            }
            actualWord = shiftRows.invShiftRows(actualWord);
            actualWord = subBytes.invSubBytes(actualWord);
            actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[0]);

            allWords.set(i, actualWord);
        }

        return dataService.removePadding(getDecryptedText(allWords));
    }

    private byte[] getDecryptedText(List<byte[][]> allWords) {
        byte[] paddedText = new byte[allWords.size() * 16];
        int index = 0;

        for (byte[][] word : allWords) {
            for (int column = 0; column < 4; column++) {
                for (int row = 0; row < 4; row++) {
                    paddedText[index++] = word[row][column];
                }
            }
        }
        return paddedText;
    }
}
