package com.peermountain.core.network.teleferique.model.body.registration;

import com.peermountain.core.network.teleferique.model.body.MessageContent;
import com.peermountain.core.network.teleferique.model.body.base.BaseBody;

/**
 * Created by Galeen on 4/4/2018.
 */
public class RegistrationMessage extends MessageContent {
    private String signature;//base64
    private String dossierSalt;//base64

    public RegistrationMessage() {
    }

    public RegistrationMessage(BaseBody body) {
        super(body);
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDossierSalt() {
        return dossierSalt;
    }

    @Override
    public void setDossierSalt(String dossierSalt) {
        this.dossierSalt = dossierSalt;
    }
}
