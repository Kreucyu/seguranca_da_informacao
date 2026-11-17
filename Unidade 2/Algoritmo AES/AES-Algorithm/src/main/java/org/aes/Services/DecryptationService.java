package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;
import org.aes.Models.State;

import java.io.*;
import java.util.Arrays;
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

    private byte[][] decryptBlock(byte[][] actualWord, byte[][][] roundKeys, byte[][] previousCipher) {
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
        if(previousCipher != null) {
            actualWord = addRoundKey.addRoundkey(actualWord, previousCipher);
        }
        return actualWord;
    }

    public byte[] decrypt(CypherData data) {
        if(data.getBytedText() == null || data.getBytedText().length == 0 || data.getBytedText().length % 16 != 0) {
            throw new IllegalArgumentException("\nTamanho inválido para texto cifrado");
        }
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        List<byte[][]> allWords = dataService.getWordsMatrix(data.getBytedText());
        List<byte[][]> originalWords = data.getOperationMode().equals("CBC")
                ? dataService.getWordsMatrix(data.getBytedText())
                : null;

        for(int i = 0; i < allWords.size(); i++) {
            byte[][] previousCipher = null;
            if(data.getOperationMode().equals("CBC")) {
                previousCipher = (i == 0)
                        ? State.getMatrix(data.getInitializationVector())
                        : originalWords.get(i - 1);
            }
            byte[][] decrypted = decryptBlock(allWords.get(i), roundKeys, previousCipher);
            allWords.set(i, decrypted);
        }

        return dataService.removePadding(getDecryptedText(allWords));
    }

    public void decryptFile(CypherData data) throws IOException {
        long fileSize = data.getInputFile().length();
        if(fileSize == 0 || fileSize % 16 != 0) {
            throw new IllegalArgumentException("\nTamanho inválido para arquivo cifrado");
        }
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        boolean isCBC = data.getOperationMode().equals("CBC");
        byte[][] previousCipher = isCBC ? State.getMatrix(data.getInitializationVector()) : null;
        File outFile = data.getOutputFile();

        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(data.getInputFile()));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {

            byte[] buf = new byte[16];
            byte[] next = new byte[16];
            int read = readFull(in, buf);

            while(read == 16) {
                int nextRead = readFull(in, next);
                byte[][] cipherBlock = dataService.bytesToBlock(buf);
                byte[][] decrypted = decryptBlock(cipherBlock, roundKeys, previousCipher);
                byte[] raw = dataService.blockToBytes(decrypted);

                if(nextRead == 0) {
                    byte[] stripped = dataService.removePaddingFromBlock(raw);
                    out.write(stripped);
                    break;
                }
                if(isCBC) previousCipher = cipherBlock;
                out.write(raw);
                byte[] tmp = buf; buf = next; next = tmp;
                read = nextRead;
            }

        } catch(Exception e) {
            if(outFile != null) outFile.delete();
            if(e instanceof IOException) throw (IOException) e;
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private int readFull(InputStream in, byte[] buf) throws IOException {
        int total = 0;
        while(total < buf.length) {
            int r = in.read(buf, total, buf.length - total);
            if(r < 0) break;
            total += r;
        }
        return total;
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
