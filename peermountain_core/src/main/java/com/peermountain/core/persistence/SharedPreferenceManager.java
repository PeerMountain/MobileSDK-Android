package com.peermountain.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.peermountain.core.model.PmAccessToken;
import com.peermountain.core.model.PublicUser;
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
    private static final String PREF_TRIPS = "PREF_TRIPS";
    private static final String PREF_TAGS = "PREF_TAGS";
    private static final String PREF_DEMO_TICKETS_SHOWN = "PREF_DEMO_TICKETS_SHOWN";
    private static final String PREF_MY_CONTACTS = "PREF_MY_CONTACTS";
    private static final String KEY_MY_LAST_MESSAGES = "my_last_messages";

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("peer_mountain_core", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }



    public static void savePublicUser(String user) {
        if(getContext()==null) return;
        SharedPreferences.Editor mEditor = getEditor(getContext());
        mEditor.putString(PREF_LI_USER, user);
        mEditor.apply();
    }

    public static PublicUser getPublicUser() {
        if(getContext()==null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        try {
            return MyJsonParser.readPublicUser(mSharedPreferences.getString(PREF_LI_USER, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static void savePmAccessToken(PmAccessToken accessToken) {
        if(getContext()==null) return;
        SharedPreferences.Editor mEditor = getEditor(getContext());
        mEditor.putString(PREF_LI_TOKEN, accessToken == null ? null : accessToken.getAccessTokenValue());
        mEditor.putLong(PREF_LI_EXPIRES, accessToken == null ? 0 : accessToken.getExpiresOn());
        mEditor.apply();
    }

    static PmAccessToken getPmAccessToken() {
        if(getContext()==null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        String token = mSharedPreferences.getString(PREF_LI_TOKEN, null);
        if (token == null)
            return null;
        long expiresOn = mSharedPreferences.getLong(PREF_LI_EXPIRES, 0);
        return new PmAccessToken(token, expiresOn);
    }


    static void logout() {
        if(getContext()==null) return;
        SharedPreferences prefs = getPrefs(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_LI_EXPIRES);
        editor.remove(PREF_LI_TOKEN);
        editor.remove(PREF_LI_USER);
        editor.commit();
    }

//    this below do not remove on logout


    static String getDeviceId() {
        if(getContext()==null) return null;
        SharedPreferences mSharedPreferences = getPrefs(getContext());
        String token = mSharedPreferences.getString("device", null);
        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("device",token);
            editor.apply();
        }
        return token;
    }

    private static Context getContext(){
        if(PersistenceManager.applicationContext==null){
            LogUtils.e("SharedPreferenceManager","No Context!");
        }
        return PersistenceManager.applicationContext;
    }
}
