package com.peermountain.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.unguarded.Keywords;
import com.peermountain.core.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * Created by SmartIntr on 8.4.2016 г..
 */
class SharedPreferenceManager {
    private static final String PREF_FINGERPRINT = "PREF_FINGERPRINT";
    private static final String PREF_PIN = "PREF_PIN";
    private static final String PREF_LI_USER = "PREF_LI_USER";
    private static final String PREF_LI_TOKEN = "PREF_LI_TOKEN";
    private static final String PREF_LI_EXPIRES = "PREF_LI_EXPIRES";
    private static final String PREF_CONFIG = "config";
    private static final String PREF_KEYWORDS = "PREF_Keywords";
    private static final String PREF_KEYWORDS_OBJECT = "PREF_KEYWORDS_OBJECT";
    private static final String PREF_PROFILE = "PREF_PROFILE";
    private static final String PREF_MY_CONTACTS = "PREF_MY_CONTACTS";
    public static final String PREF_MY_DOCUMENTS = "PREF_MY_DOCUMENTS";
    private static final String KEY_MY_LAST_MESSAGES = "my_last_messages";
    public static final String PREF_TUTO = "tuto";

    private static SecurePreferences preferencesSecure;

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("peer_mountain_core", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }

    // TODO: 10/17/2017 change the logic to get the key from OS
    private static SecurePreferences getSecurePrefs(Context context) {
        if (preferencesSecure == null) {
            preferencesSecure = new SecurePreferences(context, "peer_mountain_core_s",
                    "key", true);
        }
        return preferencesSecure;
    }

    private static SharedPreferences.Editor getSecureEditor(Context context) {
        return getSecurePrefs(context).edit();
    }

    static void saveProfile(String profile) {
        if (getContext() == null) return;
        putString(PREF_PROFILE, profile);
    }

    static Profile getProfile() {
        if (getContext() == null) return null;
        try {
            return MyJsonParser.readProfile(getString(PREF_PROFILE, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void saveDocument(AppDocument document) {
        DaoDocument.saveDocument(document);
    }

    static void addDocument(AppDocument document) {
        DaoDocument.addDocument(document);
    }
    static ArrayList<AppDocument> getDocuments(){
        return DaoDocument.getDocuments();
    }
    static void saveDocuments(ArrayList<AppDocument> documents) {
        DaoDocument.saveDocuments(documents);
    }

    static void removeDocument(String id) {
        DaoDocument.removeDocument(id);
    }


    static void saveContact(Contact contact) {
        if (getContext() == null) return;
        try { //todo remove all contacts from logout
            putString(contact.getId(), MyJsonParser.writeContact(contact));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Contact getContact(String id) {
        if (getContext() == null) return null;
        try {
            return MyJsonParser.readProfile(getString(id, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void saveContacts(Set<Contact> contacts) {
        if (getContext() == null) return;
        try {
            putString(PREF_MY_CONTACTS, MyJsonParser.writeContacts(contacts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Set<Contact> getContacts() {
        if (getContext() == null) return null;
        try { // TODO: 10/26/2017 get from here ids and take them one by one with getContact(id)
            return MyJsonParser.readContacts(getString(PREF_MY_CONTACTS, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static void savePin(String pin) {
        if (getContext() == null) return;
        putString(PREF_PIN, pin);
    }

    static String getPin() {
        if (getContext() == null) return null;
        return getString(PREF_PIN, null);
    }

    static void saveFingerprint(boolean enabled) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getSecureEditor(getContext());
        mEditor.putBoolean(PREF_FINGERPRINT, enabled);
        mEditor.apply();
    }

    static boolean getFingerprint() {
        if (getContext() == null) return false;
        return getBoolean(PREF_FINGERPRINT, false);
    }

    static void saveKeywords(String keywords) {
        if (getContext() == null) return;
        putString(PREF_KEYWORDS, keywords);
    }

    static String getKeywords() {
        if (getContext() == null) return null;
        return getString(PREF_KEYWORDS, null);
    }

    static void saveKeywords(Keywords keywords) {
        if (getContext() == null) return;
        putString(PREF_KEYWORDS_OBJECT, new Gson().toJson(keywords));
    }

    static Keywords getKeywordsAsObject() {
        if (getContext() == null) return null;
        return new Gson().fromJson(getString(PREF_KEYWORDS_OBJECT, null), Keywords.class);
    }

    static void savePmAccessToken(PmAccessToken accessToken) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getSecureEditor(getContext());
        putString(PREF_LI_TOKEN, accessToken == null ? null : accessToken.getAccessTokenValue());
        mEditor.putLong(PREF_LI_EXPIRES, accessToken == null ? 0 : accessToken.getExpiresOn());
        mEditor.apply();
    }

    static PmAccessToken getPmAccessToken() {
        if (getContext() == null) return null;
        SecurePreferences mSharedPreferences = getSecurePrefs(getContext());
        String token = getString(PREF_LI_TOKEN, null);
        if (token == null)
            return null;
        long expiresOn = mSharedPreferences.getLong(PREF_LI_EXPIRES, 0);
        return new PmAccessToken(token, expiresOn);
    }

    static void logoutPublicProfile() {
        if (getContext() == null) return;
        SecurePreferences prefs = getSecurePrefs(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_LI_EXPIRES);
        editor.remove(PREF_LI_TOKEN);
        editor.remove(PREF_LI_USER);
//        editor.remove(PREF_PIN);
//        editor.remove(PREF_CONFIG);
        editor.commit();
    }

    static void logout() {
        if (getContext() == null) return;
        logoutPublicProfile();
        SecurePreferences prefs = getSecurePrefs(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREF_FINGERPRINT);
        editor.remove(PREF_PROFILE);
        editor.remove(PREF_KEYWORDS);
        editor.remove(PREF_PIN);
        editor.remove(PREF_KEYWORDS_OBJECT);
        editor.remove(PREF_MY_CONTACTS);
        editor.remove(PREF_MY_DOCUMENTS);
//        editor.remove(PREF_CONFIG);//keep config file not related to profile
        editor.commit();
    }

//    this below do not remove on logoutPublicProfile


    static void saveConfig(PeerMountainConfig config) {
        if (getContext() == null) return;
        try {
            putString(PREF_CONFIG, MyJsonParser.writeConfig(config));
//            mEditor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static PeerMountainConfig getConfig(Context context) {
        if (context == null) {
            context = getContext();
        }
        if (context == null) return null;
        try {
            return MyJsonParser.readConfig(getString(context, PREF_CONFIG, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void saveTutoSeen() {
        if (getContext() == null) return;
        getPrefs(getContext()).edit().putBoolean(PREF_TUTO, true).apply();
    }

    static boolean isTutoSeen() {
        if (getContext() == null) return false;
        return getPrefs(getContext()).getBoolean(PREF_TUTO, false);
    }

    static String getDeviceId() {
        if (getContext() == null) return null;
        String token = getString("device", null);
        if (token == null) {
            token = UUID.randomUUID().toString();
            putString("device", token);
        }
        return token;
    }

     static void putString(String key, String value) {
        if (getContext() == null) return;
        SharedPreferences.Editor mEditor = getSecureEditor(getContext());
//        try {
        mEditor.putString(key, value);
//            mEditor.apply();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

     static String getString(String key, String opt) {
        return getString(getContext(), key, opt);
    }

     static String getString(Context context, String key, String opt) {
        SecurePreferences mSharedPreferences = getSecurePrefs(context);
        String res = mSharedPreferences.getString(key, opt);
        // TODO: 10/17/2017 remove after all users migrated in next versions
        if (res == null || res.equals(opt)) {//check if is not in plain prefs
            SharedPreferences shp = getPrefs(context);
            res = shp.getString(key, opt);
            if (res != null && !res.equals(opt)) {//migrate to secure prefs
                SharedPreferences.Editor mEditor = getSecureEditor(context);
                mEditor.putString(key, res);
                //remove form plain prefs
                SharedPreferences.Editor mEditorPlain = getEditor(context);
                mEditorPlain.remove(key);
                mEditorPlain.apply();
            }
        }
        return res;
    }

     static boolean getBoolean(String key, boolean opt) {
        SecurePreferences mSharedPreferences = getSecurePrefs(getContext());
        boolean res = mSharedPreferences.getBoolean(key, opt);
        // TODO: 10/17/2017 remove after all users migrated in next versions
        if (res == opt) {//check if is not in plain prefs
            SharedPreferences shp = getPrefs(getContext());
            res = shp.getBoolean(key, opt);
            if (res != opt) {//migrate to secure prefs
                SharedPreferences.Editor mEditor = getSecureEditor(getContext());
                mEditor.putBoolean(key, res);
                //remove form plain prefs
                SharedPreferences.Editor mEditorPlain = getEditor(getContext());
                mEditorPlain.remove(key);
                mEditorPlain.apply();
            }
        }
        return res;
    }

     static Context getContext() {
        if (PeerMountainManager.applicationContext == null) {
            LogUtils.e("SharedPreferenceManager", "No Context!");
        }
        return PeerMountainManager.applicationContext;
    }
}
