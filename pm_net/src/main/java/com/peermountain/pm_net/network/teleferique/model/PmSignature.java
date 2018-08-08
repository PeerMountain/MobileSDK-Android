package com.peermountain.pm_net.network.teleferique.model;

import com.peermountain.pm_net.secure.SecureHelper;

import java.io.Serializable;

/**
 * Created by Galeen on 3/16/2018.
 */

public class PmSignature implements Serializable{

    private String signature;
    private String timestamp;

    public PmSignature() {
    }

    public PmSignature(byte[] signature, String timestamp) {
        this.signature = SecureHelper.toBase64String(signature);
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
