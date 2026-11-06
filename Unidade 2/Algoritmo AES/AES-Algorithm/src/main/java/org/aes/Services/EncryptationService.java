package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;
import org.aes.Models.State;

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
        keyExpansion.keyExpansion(State.getMatrix(data.getKey()));




        addRoundKey.addRoundkey();
        mixColumns.mixColumns();
        shiftRows.shiftRows();
        subBytes.subBytes();
    }
}
