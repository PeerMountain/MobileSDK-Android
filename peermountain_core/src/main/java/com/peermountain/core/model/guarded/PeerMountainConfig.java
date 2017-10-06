package com.peermountain.core.model.guarded;

import android.content.Context;

/**
 * Created by Galeen on 10/4/2017.
 */

public class PeerMountainConfig {
    private boolean debug = false;
    private Context applicationContext;
    private String idCheckLicense = null;

    public PeerMountainConfig() {
    }

    public boolean isDebug() {
        return debug;
    }

    public PeerMountainConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public PeerMountainConfig setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public String getIdCheckLicense() {
        return idCheckLicense;
    }

    public PeerMountainConfig setIdCheckLicense(String idCheckLicense) {
        this.idCheckLicense = idCheckLicense;
        return this;
    }
}
