package com.peermountain.core.network.teleferique.model;

import java.io.Serializable;

/**
 * Created by Galeen on 3/16/2018.
 */

public class PmSignature implements Serializable{

    private byte[] signature;
    private String timestamp;

    public PmSignature() {
    }

    public PmSignature(byte[] signature, String timestamp) {
        this.signature = signature;
        this.timestamp = timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
