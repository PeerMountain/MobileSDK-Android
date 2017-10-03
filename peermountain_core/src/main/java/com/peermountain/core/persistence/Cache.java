package com.peermountain.core.persistence;


import android.support.annotation.RestrictTo;

import com.peermountain.core.model.PmAccessToken;
import com.peermountain.core.model.PublicUser;


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
}


