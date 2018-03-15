package com.peermountain.core.network.teleferique.model;

import java.util.List;

/**
 * Created by Galeen on 3/15/2018.
 */

public class PrivateEnvelope extends PublicEnvelope {

    public List<ACL> acl; // [{ reader; // (enc) key }]

    public String containerHash; // sha256
    public String containerSig; // RSA signature
    public String objectContainer; // (enc) BLOB
    public String objectHash; // sha256
    public List<String> Metahashes; // [hmac-sha256]
}
