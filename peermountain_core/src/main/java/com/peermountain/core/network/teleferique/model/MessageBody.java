package com.peermountain.core.network.teleferique.model;

import com.google.gson.Gson;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

/**
 * Created by Galeen on 3/14/2018.
 */

class MessageBody {
    private String messageBody;
    private transient String bodyHash;
    private transient MessageBodyObject body;

    public MessageBody(MessageBodyObject body) {
        this.body = body;
        build();
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getBodyHash() {
        return bodyHash;
    }

    public void build(){
        messageBody = SecureHelper.parse(body);
        bodyHash = SecureHelper.sha256AsBase64String(messageBody);
        LogUtils.d("messageBodyAsJson", new Gson().toJson(body));
//        LogUtils.d("fullMessageBodyAsJson", new Gson().toJson(this));
    }
}
