package com.peermountain.core.network.teleferique.model;

/**
 * Created by Galeen on 3/14/2018.
 */

public class PublicEnvelope {
    public String messageHash; // sha256
    public String messageType; // typecode , from TfConstants
    public String dossierHash; // hmac-sha256
    public String bodyHash; //sha256
    public String messageSig; // RSA signature
    public String message; // (enc)BLOB - AESEncryptedBlob!
    public String sender; // PM Address

}
