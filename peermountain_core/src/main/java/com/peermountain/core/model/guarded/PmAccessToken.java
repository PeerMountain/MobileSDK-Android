package com.peermountain.core.model.guarded;

/**
 * Created by Galeen on 10/3/2017.
 * Keep same data as LinkedIn.AccessToken
 */

public class PmAccessToken {
    private final String accessTokenValue;
    private final long expiresOn;

    public PmAccessToken(String accessTokenValue, long expiresOn) {
        this.accessTokenValue = accessTokenValue;
        this.expiresOn = expiresOn;
    }

    public String getAccessTokenValue() {
        return accessTokenValue;
    }

    public long getExpiresOn() {
        return expiresOn;
    }
}
