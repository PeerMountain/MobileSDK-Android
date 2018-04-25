package com.peermountain.core.network.teleferique.model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Galeen on 3/14/2018.
 */

public class MessageForSignature implements Serializable{
    public String messageHash;
    public String timestamp;

    public MessageForSignature(String messageHash, String timestamp) {
        this.messageHash = messageHash;//.getBytes();
        this.timestamp = timestamp;
    }

    public SortedMap<String, Object> getAsMap(){//or LinkedHashMap
        SortedMap<String, Object> resMap = new TreeMap<>();
        resMap.put("messageHash",messageHash);
        resMap.put("timestamp",timestamp);
        return resMap;
    }
}
