package com.peermountain.core.secure;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Galeen on 3/16/2018.
 */

public class CoderAES {
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";
    private static Cipher writerAes;
    private static Cipher readerAes;

    static String encrypt(String pass, String value) {
        Cipher writer = getAesWriter(pass);
        if (writer == null) return null;
        try {
            byte[] secureValue = writer.doFinal(value.getBytes(CHARSET));
            // TODO: 3/19/18 check to return just String instead
            return Base64.encodeToString(secureValue, Base64.NO_WRAP);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String decrypt(String pass, String securedEncodedValue) {
        Cipher reader = getAesReader(pass);
        if (reader == null) return null;
        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
        try {
            byte[] value = reader.doFinal(securedValue);
            return new String(value, CHARSET);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cipher getAesWriter(String pass) {
        if (writerAes == null) {
            try {
                writerAes = Cipher.getInstance(AES_TRANSFORMATION);
                initCiphers(pass, writerAes, Cipher.ENCRYPT_MODE);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return writerAes;
    }

    private static Cipher getAesReader(String pass) {
        if (readerAes == null) {
            try {
                readerAes = Cipher.getInstance(AES_TRANSFORMATION);
                initCiphers(pass, readerAes, Cipher.DECRYPT_MODE);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return readerAes;
    }

    private static void initCiphers(String secureKey, Cipher cipher, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv(cipher);
        SecretKeySpec secretKey = getSecretKey(secureKey);

        cipher.init(mode, secretKey, ivSpec);
    }

    private static IvParameterSpec getIv(Cipher cipher) {
        byte[] iv = new byte[cipher.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, iv, 0, cipher.getBlockSize());
        return new IvParameterSpec(iv);
    }

    private static SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, AES_TRANSFORMATION);
    }

    private static byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }
}
