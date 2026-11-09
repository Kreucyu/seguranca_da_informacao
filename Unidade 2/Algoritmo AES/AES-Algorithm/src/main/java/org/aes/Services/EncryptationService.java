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

    public EncryptationService() {
        this.addRoundKey = new AddRoundKey();
        this.keyExpansion = new KeyExpansion();
        this.mixColumns = new MixColumns();
        this.shiftRows = new ShiftRows();
        this.subBytes = new SubBytes();
    }

    public void encrypt(CypherData data) {
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        byte[] paddedText = applyPadding(data.getBytedText());
        List<byte[][]> allWords = getWordsMatrix(paddedText);

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
    }

    private byte[] applyPadding(byte[] originalText) {
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

    private List<byte[][]> getWordsMatrix(byte[] paddedText) {
        List<byte[][]> allWords = new ArrayList<>();
        int quantity = Math.round((float) paddedText.length + 15) / 16;

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
