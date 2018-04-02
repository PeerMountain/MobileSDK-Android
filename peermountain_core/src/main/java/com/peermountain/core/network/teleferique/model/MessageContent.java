package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.network.teleferique.model.body.MessageBody;

/**
 * Created by Galeen on 3/19/18.
 */

public class MessageContent {
    private int messageType;
    private MessageBody body;

    public MessageContent(int messageType, MessageBody body) {
        this.messageType = messageType;
        this.body = body;
    }
}
