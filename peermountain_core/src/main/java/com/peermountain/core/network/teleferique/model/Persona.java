package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.secure.SecureHelper;

import java.security.PublicKey;

/**
 * Created by Galeen on 4/5/2018.
 */
public class Persona {
    private PublicKey pubKey;
    private String address, nickname, pubkey;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = SecureHelper.getKey(pubkey);
        ;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
