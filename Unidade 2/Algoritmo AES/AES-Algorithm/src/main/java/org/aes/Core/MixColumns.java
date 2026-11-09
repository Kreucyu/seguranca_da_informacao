package org.aes.Core;

import org.aes.Models.Tables;

import java.util.Objects;

public class MixColumns {
    public MixColumns() {}

    public byte[][] mixColumns(byte[][] wordMatrix) {
        return doMix(wordMatrix, "default");
    }

    public byte[][] invMixColumns(byte[][] wordMatrix) {
        return doMix(wordMatrix, "inverted");
    }

    private byte[][] doMix(byte[][] wordMatrix, String type) {
        byte[][] mixedMatrix = new byte[4][4];

        for(int row = 0; row < 4; row++) {
            for(int col = 0; col < 4; col++) {
                int value = 0;
                for(int i = 0; i < 4; i++) {
                    int term = wordMatrix[i][col];
                    value ^= galoisMultiplication
                            (term, Objects.equals(type, "inverted")
                                    ? Tables.INVERTED_MULTIPLICATION_MATRIX[row][i]
                                    : Tables.MULTIPLICATION_MATRIX[row][i]);
                }
                mixedMatrix[row][col] = (byte) value;
            }
        }
        return mixedMatrix;
    }

    private int galoisMultiplication(int term, int matrixValue) {
        if(term == 0 || matrixValue == 0) return 0;
        if(term == 1) return matrixValue;
        if(matrixValue == 1) return term;

        int rowTerm = (term & 0xF0) >> 4;
        int colTerm = term & 0x0F;

        int rowMatrixValue = (matrixValue & 0xF0) >> 4;
        int colMatrixValue = matrixValue & 0x0F;

        int sum = Tables.TABLE_L[rowTerm][colTerm] + Tables.TABLE_L[rowMatrixValue][colMatrixValue];

        if(sum > 0xFF) sum -= 0xFF;

        int rowSum = (sum & 0xF0) >> 4;
        int colSum = sum & 0x0F;

        return Tables.TABLE_E[rowSum][colSum];
    }
}
