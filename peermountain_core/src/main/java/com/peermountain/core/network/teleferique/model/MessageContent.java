package com.peermountain.core.network.teleferique.model;

/**
 * Created by Galeen on 3/19/18.
 */

public class MessageContent {
    private int messageType;
    private com.peermountain.core.network.teleferique.model.body.MessageContent body;

    public MessageContent(int messageType, com.peermountain.core.network.teleferique.model.body.MessageContent body) {
        this.messageType = messageType;
        this.body = body;
    }
}
