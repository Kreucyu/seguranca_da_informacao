package org.aes.Core;

public class ShiftRows {
    public ShiftRows() {}

    public byte[][] shiftRows(byte[][] wordMatrix) {
        for(int i = 1; i < 4; i++) {
            byte[] originalRow = wordMatrix[i].clone();
            for(int j = 0; j < 4; j++) {
                wordMatrix[i][j] = originalRow[(j + i) % 4];
            }
        }
        return wordMatrix;
    }

    public void invShiftRows() {}
}
