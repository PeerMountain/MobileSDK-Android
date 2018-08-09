package com.peermountain.core.model.guarded;

import android.content.Context;

/**
 * Created by Galeen on 10/4/2017.
 */

public class PeerMountainConfig {
    private boolean debug = false;
    private Context applicationContext;
    private String idCheckLicense = null;
    private long userValidTime = 1000*60*5;//5min
    private int fontSize = 14;
    private String apiScanKey;

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

    public long getUserValidTime() {
        return userValidTime;
    }

    public PeerMountainConfig setUserValidTime(long userValidTime) {
        this.userValidTime = userValidTime;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    /**
     * Set font size in SP
     * @param fontSize the number will be converted in SP pixels if not set default value is 14
     * @return self
     */
    public PeerMountainConfig setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getApiScanKey() {
        return apiScanKey;
    }

    public PeerMountainConfig setApiScanKey(String apiScanKey) {
        this.apiScanKey = apiScanKey;
        return this;
    }
}
