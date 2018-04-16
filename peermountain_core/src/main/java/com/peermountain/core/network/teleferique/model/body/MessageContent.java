package com.peermountain.core.network.teleferique.model.body;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

/**
 * Created by Galeen on 3/14/2018.
 */

public class MessageContent {
    private String messageBody;//Base64
    private int bodyType;//from TfConstants
    public transient String bodyHash, time;
    public transient byte[] messageBodyPacked;

    public MessageContent() {
    }

    public MessageContent(MessageContent messageContent) {
        this.messageBody = messageContent.getMessageBody();
        this.bodyType = messageContent.getBodyType();
    }

    public MessageContent(MessageBodyObject body) {
        bodyType = body.takeBodyType();
        time = body.takeTime();
        build(body);
    }

    public String getMessageBody() {
        return messageBody;
    }

    public int getBodyType() {
        return bodyType;
    }

    public void build(MessageBodyObject body) {
        messageBodyPacked = SecureHelper.parse(body);
        messageBody = SecureHelper.toBase64String(messageBodyPacked);
        bodyHash = SecureHelper.sha256AsBase64String(messageBody);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        LogUtils.d("message_body AsJson", gson.toJson(body));
        LogUtils.d("message AsJson", gson.toJson(this));
//        LogUtils.d("fullMessageBodyAsJson", new Gson().toJson(this));
    }

    public String encrypt(String pass){
        return null;
    }
}
