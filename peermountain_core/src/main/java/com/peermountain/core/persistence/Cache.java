package com.peermountain.core.persistence;


import android.support.annotation.RestrictTo;

import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PublicUser;


/**
 * Created by Galeen on 28.9.2016 г..
 * Data holder for fast access
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class Cache {
    private static Cache instance;
    private String sessionToken = null;
    private PmAccessToken accessToken;
    private PublicUser publicUser;
    private PeerMountainConfig config;
    private String pin;
    private boolean fingerprint=false;

    private Cache() {
    }
    static Cache getInstance() {
        if (instance == null)
            instance = new Cache();
        return instance;
    }

    void clearCache() {
        instance = null;
    }

    void clearPublicProfileCache() {
        if(instance==null) return;
        instance.publicUser = null;
        instance.accessToken = null;
    }


    PmAccessToken getAccessToken() {
        return accessToken;
    }

    void setAccessToken(PmAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    PublicUser getPublicUser() {
        return publicUser;
    }

    void setPublicUser(PublicUser publicUser) {
        this.publicUser = publicUser;
    }

    public PeerMountainConfig getConfig() {
        return config;
    }

    public void setConfig(PeerMountainConfig config) {
        this.config = config;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean isFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }
}


