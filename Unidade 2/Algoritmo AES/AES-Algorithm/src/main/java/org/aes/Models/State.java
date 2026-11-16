package org.aes.Models;

public class State {
    public State() {}

    public static byte[][] getMatrix(byte[] key) {

        byte[][] matrix = new byte[4][4];

        int k = 0;
        for(int col = 0; col < 4; col++) {
            for(int row = 0; row < 4; row++) {
                if(k < key.length) {
                    matrix[row][col] = key[k];
                    k++;
                }
            }
        }
        return matrix;
    }
}
