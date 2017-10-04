package com.peermountain.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.utils.LogUtils;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by SmartIntr on 8.4.2016 г..
 */
class SharedPreferenceManager {
    private static final String PREF_LOGIN = "PREF_login";
    private static final String PREF_USER = "PREF_USER";
    private static final String PREF_LI_USER = "PREF_LI_USER";
    private static final String PREF_LI_TOKEN = "PREF_LI_TOKEN";
    private static final String PREF_LI_EXPIRES = "PREF_LI_EXPIRES";
    private static final String PREF_CONFIG = "config";
    private static final String PREF_TAGS = "PREF_TAGS";
    private static final String PREF_DEMO_TICKETS_SHOWN = "PREF_DEMO_TICKETS_SHOWN";
    private static final String PREF_MY_CONTACTS = "PREF_MY_CONTACTS";
    private static final String KEY_MY_LAST_MESSAGES = "my_last_messages";

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("peer_mountain_core", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }


     static void savePublicUser(String user) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getEditor(getContext());
        mEditor.putString(PREF_LI_USER, user);
        mEditor.apply();
    }

     static PublicUser getPublicUser() {
        if (getContext() == null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        try {
            return MyJsonParser.readPublicUser(mSharedPreferences.getString(PREF_LI_USER, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static void savePmAccessToken(PmAccessToken accessToken) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getEditor(getContext());
        mEditor.putString(PREF_LI_TOKEN, accessToken == null ? null : accessToken.getAccessTokenValue());
        mEditor.putLong(PREF_LI_EXPIRES, accessToken == null ? 0 : accessToken.getExpiresOn());
        mEditor.apply();
    }

    static PmAccessToken getPmAccessToken() {
        if (getContext() == null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        String token = mSharedPreferences.getString(PREF_LI_TOKEN, null);
        if (token == null)
            return null;
        long expiresOn = mSharedPreferences.getLong(PREF_LI_EXPIRES, 0);
        return new PmAccessToken(token, expiresOn);
    }

    static void logout() {
        if (getContext() == null) return;
        SharedPreferences prefs = getPrefs(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_LI_EXPIRES);
        editor.remove(PREF_LI_TOKEN);
        editor.remove(PREF_LI_USER);
//        editor.remove(PREF_CONFIG);
        editor.commit();
    }

//    this below do not remove on logout


    static void saveConfig(PeerMountainConfig config) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getEditor(getContext());
        try {
            mEditor.putString(PREF_CONFIG, MyJsonParser.writeConfig(config));
            mEditor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static PeerMountainConfig getConfig(Context context) {
        if (context == null) {
            context = getContext();
        }
        if (context == null) return null;
        SharedPreferences mSharedPreferences = getPrefs(context);
        try {
            return MyJsonParser.readConfig(mSharedPreferences.getString(PREF_CONFIG, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getDeviceId() {
        if (getContext() == null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        String token = mSharedPreferences.getString("device", null);
        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("device", token);
            editor.apply();
        }
        return token;
    }

    private static Context getContext() {
        if (PeerMountainManager.applicationContext == null) {
            LogUtils.e("SharedPreferenceManager", "No Context!");
        }
        return PeerMountainManager.applicationContext;
    }
}
