package org.aes.Models;

public class CypherData {

    private String operation;
    private String finalObjectType;
    private byte[] bytedText;
    private byte[] key;
    private String operationMode;
    private byte[] initializationVector;

    public CypherData() {}

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFinalObjectType() {
        return finalObjectType;
    }

    public void setFinalObjectType(String finalObjectType) {
        this.finalObjectType = finalObjectType;
    }

    public byte[] getBytedText() {
        return bytedText;
    }

    public void setBytedText(byte[] bytedText) {
        this.bytedText = bytedText;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }
}
