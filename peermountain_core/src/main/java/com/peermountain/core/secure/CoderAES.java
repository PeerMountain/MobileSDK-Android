package com.peermountain.core.secure;

import android.util.Base64;

import com.peermountain.core.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
    private static final String AES_TRANSFORMATION = "AES/ECB/NoPadding";//ISO10126Padding //PKCS5Padding //NoPadding
    private static final boolean WITH_IV = false;
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";
    private static Cipher writerAes;
    private static Cipher readerAes;

    // TODO: 3/19/18 check to return just String instead
    static String encrypt(String pass, String value) {
        Cipher writer = getAesWriter(pass);
        if (writer == null) return null;//value.getBytes(CHARSET));
        try {
            byte[] secureValue = writer.doFinal(customPadding(value,16));
            return Base64.encodeToString(secureValue, Base64.NO_WRAP);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
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
        SecretKeySpec secretKey = getSecretKey(secureKey);
        if (WITH_IV) {
            IvParameterSpec ivSpec = getIv(cipher);
            cipher.init(mode, secretKey, ivSpec);
        } else {
            cipher.init(mode, secretKey);//, ivSpec);
        }
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
        return customPadding(key,32);

//        byte[] arrBTmp = key.getBytes();
//        byte[] arrB = new byte[16];
//        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
//            arrB[i] = arrBTmp[i];
//        }
//        return arrB;

//        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
//        md.reset();
//        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
//        return keyBytes;
    }

    private static byte[] customPadding(String key,int key_size) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (key.length() % key_size == 0) {
            LogUtils.d("Padding", key);
            return key.getBytes();
        }
        int diff = key_size - (key.length() % key_size);
        char c = (char) (diff+ '0');
        StringBuilder sb = new StringBuilder(key);
        for (int i = 0; i < diff; i++) {
            sb.append(c);
        }
        LogUtils.d("Padding", sb.toString());
        byte[] bytes = sb.toString().getBytes(CHARSET);
        StringBuilder sb1 = new StringBuilder();
        sb1.append("[");
        for (int i = 0; i < bytes.length-1; i++) {
            sb1.append(i+" : "+bytes[i] +" , ");
        }
        sb1.append((bytes.length-1)+" : "+bytes[bytes.length-1] +" ]");
        LogUtils.d("key_bytes", sb1.toString());
        return sb.toString().getBytes(CHARSET);
    }

//    private static byte[] customUnPadding(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        int diff = key_size - (key.length() % key_size);
//        char c = (char) (diff);// + '0');
//        StringBuilder sb = new StringBuilder(key);
//        for (int i = 0; i < diff; i++) {
//            sb.append(c);
//        }
//        LogUtils.d("Padding", sb.toString());
//        return sb.toString().getBytes(CHARSET);
//    }
}
