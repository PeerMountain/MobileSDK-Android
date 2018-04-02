package com.peermountain.core.network.teleferique.model.body;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

/**
 * Created by Galeen on 3/14/2018.
 */

public class MessageBody {
    private byte[] messageBody;
    private int bodyType;//from TfConstants
    public transient String bodyHash, time;

    public MessageBody() {
    }

    public MessageBody(int bodyType) {
        this.bodyType = bodyType;
    }

    public MessageBody(MessageBodyObject body) {
        bodyType = body.takeBodyType();
        time = body.takeTime();
        build(body);
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public int getBodyType() {
        return bodyType;
    }

    public void build(MessageBodyObject body) {
        messageBody = SecureHelper.parse(body);
        bodyHash = SecureHelper.sha256AsBase64String(messageBody);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        LogUtils.d("message_body AsJson", gson.toJson(body));
        LogUtils.d("message AsJson", gson.toJson(this));
//        LogUtils.d("fullMessageBodyAsJson", new Gson().toJson(this));
    }
}
