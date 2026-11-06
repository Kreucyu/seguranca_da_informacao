package org.aes.Models;

public class State {
    public State() {}

    public static byte[][] getMatrix(byte[] key) {

        byte[][] matrix = new byte[4][4];

        int k = 0;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(k < key.length) {
                    matrix[i][j] = key[k];
                    k++;
                }
            }
        }
        return matrix;
    }
}
