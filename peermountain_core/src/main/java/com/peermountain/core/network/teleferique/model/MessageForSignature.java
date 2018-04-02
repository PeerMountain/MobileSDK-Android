package com.peermountain.core.network.teleferique.model;

import java.io.Serializable;

/**
 * Created by Galeen on 3/14/2018.
 */

public class MessageForSignature implements Serializable{
    public byte[] messageHash;
    public String timestamp;

    public MessageForSignature(byte[] messageHash, String timestamp) {
        this.messageHash = messageHash;
        this.timestamp = timestamp;
    }
}
