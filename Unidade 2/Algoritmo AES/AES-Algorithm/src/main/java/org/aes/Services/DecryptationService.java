package org.aes.Services;

import org.aes.Core.*;
import org.aes.Models.CypherData;

public class DecryptationService {
    private final AddRoundKey addRoundKey;
    private final KeyExpansion keyExpansion;
    private final MixColumns mixColumns;
    private final ShiftRows shiftRows;
    private final SubBytes subBytes;

    public DecryptationService() {
        this.addRoundKey = new AddRoundKey();
        this.keyExpansion = new KeyExpansion();
        this.mixColumns = new MixColumns();
        this.shiftRows = new ShiftRows();
        this.subBytes = new SubBytes();
    }

    public void decrypt(CypherData data) {
        addRoundKey.addRoundkey();
        keyExpansion.keyExpansion();
        mixColumns.invMixColumns();
        shiftRows.invShiftRows();
        subBytes.invSubBytes();
    }
}
