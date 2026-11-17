package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;
import org.aes.Models.State;

import java.io.*;
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

    private byte[][] encryptBlock(byte[][] actualWord, byte[][][] roundKeys, byte[][] previousCipher) {
        if(previousCipher != null) {
            actualWord = addRoundKey.addRoundkey(actualWord, previousCipher);
        }
        actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[0]);
        for(int j = 1; j < roundKeys.length; j++) {
            actualWord = subBytes.subBytes(actualWord);
            actualWord = shiftRows.shiftRows(actualWord);
            if(j != 10) {
                actualWord = mixColumns.mixColumns(actualWord);
            }
            actualWord = addRoundKey.addRoundkey(actualWord, roundKeys[j]);
        }
        return actualWord;
    }

    public byte[] encrypt(CypherData data) {
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        byte[] paddedText = dataService.applyPadding(data.getBytedText());
        List<byte[][]> allWords = dataService.getWordsMatrix(paddedText);

        byte[][] previousCipher = data.getOperationMode().equals("CBC")
                ? State.getMatrix(data.getInitializationVector())
                : null;

        for(int i = 0; i < allWords.size(); i++) {
            byte[][] encrypted = encryptBlock(allWords.get(i), roundKeys, previousCipher);
            if(data.getOperationMode().equals("CBC")) {
                previousCipher = encrypted;
            }
            allWords.set(i, encrypted);
        }
        return getEncryptedBytes(allWords);
    }

    public void encryptFile(CypherData data) throws IOException {
        byte[][][] roundKeys = keyExpansion.keyExpansion(State.getMatrix(data.getKey()));
        boolean isCBC = data.getOperationMode().equals("CBC");
        byte[][] previousCipher = isCBC ? State.getMatrix(data.getInitializationVector()) : null;
        File outFile = data.getOutputFile();

        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(data.getInputFile()));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {

            byte[] buf = new byte[16];
            byte[] next = new byte[16];
            int read = readFull(in, buf);

            if(read == 0) {
                byte[] padded = dataService.applyPaddingToBlock(buf, 0);
                byte[][] encrypted = encryptBlock(dataService.bytesToBlock(padded), roundKeys, previousCipher);
                out.write(dataService.blockToBytes(encrypted));
            } else {
                while(true) {
                    int nextRead = readFull(in, next);
                    if(nextRead == 0) {
                        byte[] padded = dataService.applyPaddingToBlock(buf, read);
                        byte[][] encrypted = encryptBlock(dataService.bytesToBlock(padded), roundKeys, previousCipher);
                        if(isCBC) previousCipher = encrypted;
                        out.write(dataService.blockToBytes(encrypted));
                        if(read == 16) {
                            byte[] extra = dataService.applyPaddingToBlock(new byte[16], 0);
                            byte[][] encExtra = encryptBlock(dataService.bytesToBlock(extra), roundKeys, previousCipher);
                            out.write(dataService.blockToBytes(encExtra));
                        }
                        break;
                    }
                    byte[] padded = dataService.applyPaddingToBlock(buf, read);
                    byte[][] encrypted = encryptBlock(dataService.bytesToBlock(padded), roundKeys, previousCipher);
                    if(isCBC) previousCipher = encrypted;
                    out.write(dataService.blockToBytes(encrypted));
                    byte[] tmp = buf; buf = next; next = tmp;
                    read = nextRead;
                }
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
