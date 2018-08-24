package com.peermountain.core.persistence;/*
Copyright (C) 2012 Sveinung Kval Bakken, sveinung.bakken@gmail.com

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


class SecurePreferences {

    public static class SecurePreferencesException extends RuntimeException {

        SecurePreferencesException(Throwable e) {
            super(e);
        }

    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_TRANSFORMATION = "AES/CBC/PKCS5Padding";//"AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";

    private final boolean encryptKeys;
    private final Cipher writer;
    private final Cipher reader;
    private final Cipher keyWriter;
    private final SharedPreferences preferences;

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
    SecurePreferences(Context context, String preferenceName, String secureKey, boolean encryptKeys) throws SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);

            initCiphers(secureKey);

            this.preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

            this.encryptKeys = encryptKeys;
        } catch (GeneralSecurityException e) {
            throw new SecurePreferencesException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    // TODO: 3/13/2018 create a method to check if this is the right key, try to read some predefined data

    private void initCiphers(String secureKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        keyWriter.init(Cipher.ENCRYPT_MODE, secretKey);
    }

    private IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, iv, 0, writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, TRANSFORMATION);
    }

    private byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }

    public void put(String key, String value) {
        if (value == null) {
            removeValue(key);
        } else {
            putValue(toKey(key), value,false);
        }
    }

    private void apply(String key, String value) {
        if (value == null) {
            preferences.edit().remove(toKey(key)).apply();
        } else {
            putValue(toKey(key), value, false);
        }
    }

    public boolean containsKey(String key) {
        return preferences.contains(toKey(key));
    }

    @SuppressLint("ApplySharedPref")
    private void removeValue(String key) {
        preferences.edit().remove(toKey(key)).commit();
    }

    public String getString(String key) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), "");
            return decrypt(securedEncodedValue);
        }
        return null;
    }

    public String getString(String key, String opt) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), opt);
            return decrypt(securedEncodedValue);
        }
        return opt;
    }

     boolean getBoolean(String key, boolean opt) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), Boolean.toString(opt));
            String res = decrypt(securedEncodedValue);
            return Boolean.parseBoolean(res);
        }
        return opt;
    }

     long getLong(String key, long opt) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), Long.toString(opt));
            String res = decrypt(securedEncodedValue);
            return Long.parseLong(res);
        }
        return opt;
    }

    @SuppressLint("ApplySharedPref")
    private void clear() {
        preferences.edit().clear().commit();
    }

    private String toKey(String key) {
        if (encryptKeys)
            return encrypt(key, keyWriter);
        else return key;
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    private void putValue(String key, String value, boolean apply) throws SecurePreferencesException {
        String secureValueEncoded = encrypt(value, writer);

        SharedPreferences.Editor  editor = preferences.edit().putString(key, secureValueEncoded);
        if(apply){
            editor.apply();
        }else{
            editor.commit();
        }
    }

    public String encrypt(String value) {
        return encrypt(value, keyWriter);
    }

    private String encrypt(String value, Cipher writer) throws SecurePreferencesException {
        byte[] secureValue;
        try {
            secureValue = convert(writer, value.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
        String secureValueEncoded = Base64.encodeToString(secureValue, Base64.NO_WRAP);
        return secureValueEncoded;
    }

    private String decrypt(String securedEncodedValue) {
        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
        byte[] value = convert(reader, securedValue);
        try {
            return new String(value, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    private static byte[] convert(Cipher cipher, byte[] bs) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        } catch (Exception e) {
            throw new SecurePreferencesException(e);
        }
    }

    SharedPreferences.Editor edit() {
        return editor;
    }

    private SharedPreferences.Editor editor = new SharedPreferences.Editor() {
        @Override
        public SharedPreferences.Editor putString(String s, @Nullable String s1) {
            SecurePreferences.this.apply(s, s1);
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String s, @Nullable Set<String> set) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putInt(String s, int i) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putLong(String s, long l) {
            SecurePreferences.this.apply(s, Long.toString(l));
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String s, float v) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String s, boolean b) {
            SecurePreferences.this.apply(s, Boolean.toString(b));
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String s) {
            removeValue(s);
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            SecurePreferences.this.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return false;
        }

        @Override
        public void apply() {

        }
    };
}
