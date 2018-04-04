package com.peermountain.core.network.teleferique.model.body.registration;

import com.peermountain.core.network.teleferique.model.body.MessageBodyObject;
import com.peermountain.core.network.teleferique.model.body.MessageContent;

/**
 * Created by Galeen on 4/4/2018.
 */
public class RegistrationMessage extends MessageContent {
    private String signature;//base64
    private String dossierSalt;//base64

    public RegistrationMessage() {
    }

    public RegistrationMessage(MessageBodyObject body) {
        super(body);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDossierSalt() {
        return dossierSalt;
    }

    public void setDossierSalt(String dossierSalt) {
        this.dossierSalt = dossierSalt;
    }
}
