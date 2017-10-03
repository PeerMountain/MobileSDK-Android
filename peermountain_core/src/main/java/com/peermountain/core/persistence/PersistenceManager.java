package com.peermountain.core.persistence;

import android.content.Context;

import com.peermountain.core.model.PmAccessToken;
import com.peermountain.core.model.PublicUser;

import java.io.IOException;


/**
 * Created by Galeen on 28.9.2016 г..
 */
public class PersistenceManager {
    // TODO: 6.1.2017 г. create a method to update the cache if is dirty
    public static Context applicationContext = null;

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
