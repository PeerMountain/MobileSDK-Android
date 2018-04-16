package com.peermountain.core.network.teleferique.model.body;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peermountain.core.network.teleferique.model.PublicEnvelope;
import com.peermountain.core.network.teleferique.model.body.base.BaseBody;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

/**
 * Created by Galeen on 3/14/2018.
 */

public class MessageContent {
    private String messageBody;//Base64
    private int bodyType;//from TfConstants
    private String bodyHash, time;
    private byte[] messageBodyPacked;
    private byte[] salt = null;
    private String messageType;//from TfConstants

    public MessageContent() {
    }

    public MessageContent(BaseBody body) {
        bodyType = body.takeBodyType();
        time = body.takeTime();
        salt = SecureHelper.generateSalt(40);
        messageType = body.takeMessageType();
        build(body);
    }
    //only get methods will be parsed
    public String getMessageBody() {
        return messageBody;
    }

    public int getBodyType() {
        return bodyType;
    }

    public void build(BaseBody body) {
        messageBodyPacked = SecureHelper.parse(body);
        messageBody = SecureHelper.toBase64String(messageBodyPacked);
        bodyHash = SecureHelper.sha256AsBase64String(messageBody);
        setDossierSalt(SecureHelper.toBase64String(salt));
        setSignature(PublicEnvelope.getMessageSignature(time, bodyHash));

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        LogUtils.d("message_body AsJson", gson.toJson(body));
        LogUtils.d("message AsJson", gson.toJson(this));
    }

    //use take instead of get to escape parsing the salt
    public byte[] takeSalt() {
        return salt;
    }

    public String encrypt(String pass) {
        return SecureHelper.encodeAES(pass, SecureHelper.parse(this));
    }

    public void setSignature(String signature) {
    }

    public void setDossierSalt(String dossierSalt) {
    }

    public String asJson() {
        return new Gson().toJson(this);
    }

    public String takeMessageType() {
        return messageType;
    }

    public byte[] takeMessageBodyPacked() {
        return messageBodyPacked;
    }

    public String takeBodyHash() {
        return bodyHash;
    }

    public String takeTime() {
        return time;
    }
}
