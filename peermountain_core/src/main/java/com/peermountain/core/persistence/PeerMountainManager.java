package com.peermountain.core.persistence;

import android.content.Context;
import android.support.annotation.RestrictTo;

import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PublicUser;

import java.io.IOException;


/**
 * Created by Galeen on 28.9.2016 г..
 */
public class PeerMountainManager {
    // TODO: 6.1.2017 г. create a method to update the cache if is dirty
    static Context applicationContext = null;

    public static void init(PeerMountainConfig config){
        PeerMountainManager.applicationContext = config.getApplicationContext();
        config.setApplicationContext(null);
        Cache.getInstance().setConfig(config);
        SharedPreferenceManager.saveConfig(config);
    }

    /**
     * Get last saved Config
     * @param context any context works to get the value
     * @return PeerMountainConfig object without applicationContext in it
     */
    public static PeerMountainConfig getLastPeerMountainConfig(Context context) {
        if (Cache.getInstance().getConfig() == null) {
            Cache.getInstance().setConfig(SharedPreferenceManager.getConfig(context));
        }
        return Cache.getInstance().getConfig();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static PeerMountainConfig getPeerMountainConfig() {
        if (Cache.getInstance().getConfig() == null) {
            Cache.getInstance().setConfig(SharedPreferenceManager.getConfig(null));
        }
        return Cache.getInstance().getConfig();
    }

    public static PmAccessToken getLiAccessToken() {
        if (Cache.getInstance().getAccessToken() == null) {
            Cache.getInstance().setAccessToken(SharedPreferenceManager.getPmAccessToken());
        }
        return Cache.getInstance().getAccessToken();
    }

    public static void saveLiAccessToken(PmAccessToken accessToken) {
        Cache.getInstance().setAccessToken(accessToken);
        SharedPreferenceManager.savePmAccessToken(accessToken);
    }



    public static String getDeviceId() {
        return SharedPreferenceManager.getDeviceId();
    }

    public static PublicUser saveLiUser(String publicUserJson) {
        PublicUser liUser = null;
        try {
            liUser = MyJsonParser.readPublicUser(publicUserJson);
            Cache.getInstance().setPublicUser(liUser);
            SharedPreferenceManager.savePublicUser(publicUserJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return liUser;
    }

    public static void saveLiUser(PublicUser publicUser) {
            Cache.getInstance().setPublicUser(publicUser);
        try {
            SharedPreferenceManager.savePublicUser(MyJsonParser.writePublicUser(publicUser));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PublicUser getPublicUser() {
        if (Cache.getInstance().getPublicUser() == null)
            Cache.getInstance().setPublicUser(SharedPreferenceManager.getPublicUser());
        return Cache.getInstance().getPublicUser();
    }


    public static void logout() {
        Cache.getInstance().clearCache();
        SharedPreferenceManager.logout();
//        Messenger.clearAll();
    }

}
