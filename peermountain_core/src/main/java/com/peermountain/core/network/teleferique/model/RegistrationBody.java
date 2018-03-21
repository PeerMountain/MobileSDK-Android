package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.network.teleferique.TfConstants;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class RegistrationBody implements MessageBodyObject{
    public String inviteMsgID; // sha256
    public String keyProof; // Teleferique pubKey(invKey)
    public String inviteName; // Teleferique pubKey(inviteName)
    public String publicKey; // RSA pubkey
    public String publicNickname; // text

    @Override
    public int takeBodyType() {
        return TfConstants.BODY_TYPE_REGISTRATION;
    }
}