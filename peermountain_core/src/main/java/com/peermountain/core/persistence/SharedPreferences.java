package com.peermountain.core.persistence;

import android.content.Context;

/**
 * Created by Galeen on 10/17/2017.
 */

public class SharedPreferences extends SecurePreferences {
    /**
     * This will initialize an instance of the SecurePreferences class
     *
     * @param context        your current context.
     * @param preferenceName name of preferences file (preferenceName.xml)
     * @param secureKey      the key used for encryption, finding a good key scheme is hard.
     *                       Hardcoding your key in the application is bad, but better than plaintext preferences. Having the user enter the key upon application launch is a safe(r) alternative, but annoying to the user.
     * @param encryptKeys    settings this to false will only encrypt the values,
     *                       true will encrypt both values and keys. Keys can contain a lot of information about
     *                       the plaintext value of the value which can be used to decipher the value.
     * @throws SecurePreferencesException
     */
    public SharedPreferences(Context context, String preferenceName, String secureKey, boolean encryptKeys) throws SecurePreferencesException {
        super(context, preferenceName, secureKey, encryptKeys);
    }

}
