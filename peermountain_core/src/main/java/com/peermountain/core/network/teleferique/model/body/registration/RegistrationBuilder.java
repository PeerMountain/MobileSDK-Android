package com.peermountain.core.network.teleferique.model.body.registration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.PublicEnvelope;
import com.peermountain.core.network.teleferique.model.SendObject;
import com.peermountain.core.network.teleferique.model.body.MessageContent;
import com.peermountain.core.network.teleferique.model.body.base.BaseBuilder;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * Created by Galeen on 4/2/18.
 */
public class RegistrationBuilder extends BaseBuilder {
    private String inviteMsgID; // sha256 from invite
    private String keyProof; // Teleferique pubKey RSA(invKey)
    private String inviteName; // Teleferique pubKey RSA(inviteName)
    private String publicKey; // our RSA pubkey
    private String publicNickname; // text

    public RegistrationBuilder() {
        super(TfConstants.BODY_TYPE_REGISTRATION, null);
    }

    public RegistrationBuilder(int bodyType, String time) {
        super(bodyType, time);
    }

    @Override
    public MessageContent getMessageContent() {
        return new RegistrationMessage(new RegistrationBody(this));
    }

    @Override
    public SendObject build() {
        prepare();
        PublicEnvelope envelope = new PublicEnvelope(this);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String variables = gson.toJson(envelope, PublicEnvelope.class);
//        .replace("\\\\u003d","=");

        return new SendObject().setQuery(
                TfConstants.TELEFERIC_QUERY)
                .setVariables(variables);
    }

    private void prepare() {
        //AES encrypt body with passphrase='Peer Mountain'
        PublicKey serverPublicKey = SecureHelper.getPublicKey(TfConstants.KEY_PUBLIC_SERVER);
        setInviteName(SecureHelper.encryptRSAb64(getInviteName(), serverPublicKey));
        LogUtils.d("invite name encoded", getInviteName());
        setKeyProof(SecureHelper.encryptRSAb64(getKeyProof(), serverPublicKey));
        LogUtils.d("KeyProof encoded", getKeyProof());
        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(PeerMountainManager.getApplicationContext(), TfConstants.KEY_ALIAS);
        publicKey = SecureHelper.toPEM(keyPair.getPublic());
        LogUtils.d("publicKey", publicKey);
    }

    public String getInviteMsgID() {
        return inviteMsgID;
    }

    public RegistrationBuilder setInviteMsgID(String inviteMsgID) {
        this.inviteMsgID = inviteMsgID;
        return this;
    }

    public String getKeyProof() {
        return keyProof;
    }

    public RegistrationBuilder setKeyProof(String keyProof) {
        this.keyProof = keyProof;
        return this;
    }

    public String getInviteName() {
        return inviteName;
    }

    public RegistrationBuilder setInviteName(String inviteName) {
        this.inviteName = inviteName;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public RegistrationBuilder setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String getPublicNickname() {
        return publicNickname;
    }

    public RegistrationBuilder setPublicNickname(String publicNickname) {
        this.publicNickname = publicNickname;
        return this;
    }
}
