package com.peermountain.core.network.teleferique.model.body.registration;

import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.SendObject;
import com.peermountain.core.network.teleferique.model.body.base.BaseBuilder;

/**
 * Created by Galeen on 4/2/18.
 */
public class RegistrationBuilder extends BaseBuilder {
    private String inviteMsgID; // sha256
    private String keyProof; // Teleferique pubKey(invKey)
    private String inviteName; // Teleferique pubKey(inviteName)
    private String publicKey; // RSA pubkey
    private String publicNickname; // text

    public RegistrationBuilder() {
        super(TfConstants.BODY_TYPE_REGISTRATION,null);
    }

    public RegistrationBuilder(int bodyType, String time) {
        super(bodyType, time);
    }

    @Override
    public SendObject build() {
        return null;
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
