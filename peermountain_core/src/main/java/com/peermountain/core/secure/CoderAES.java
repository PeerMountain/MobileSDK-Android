package com.peermountain.core.secure;

import android.util.Base64;

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
    public static final String CHARSET = "UTF-8";
    private static Cipher writerAes;
    private static Cipher readerAes;

    // TODO: 3/19/18 check to return just String instead
    static String encrypt(String pass, String value) {
        try {
            return encrypt(pass, value.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Cipher writer = getAesWriter(pass);
//        if (writer == null) return null;
//        try {
//            byte[] secureValue = writer.doFinal(value.getBytes(CHARSET));//customPadding(value,16));
//            return Base64.encodeToString(secureValue, Base64.NO_WRAP);
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    static String encrypt(String pass, byte[] value) {
        Cipher writer = getAesWriter(pass);
        if (writer == null) return null;
        try {
            byte[] secureValue = writer.doFinal(customPadding(value, 16));
            return Base64.encodeToString(secureValue, Base64.NO_WRAP);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String decrypt(String pass, String securedEncodedValue) {
        try {
            return new String(decrypt(pass,Base64.decode(securedEncodedValue, Base64.NO_WRAP)), CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Cipher reader = getAesReader(pass);
//        if (reader == null) return null;
//        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
//        try {
//            byte[] value = reader.doFinal(securedValue);
//            return new String(value, CHARSET);
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    static byte[]  decrypt(String pass, byte[] securedValue) {
        Cipher reader = getAesReader(pass);
        if (reader == null) return null;
        try {
            byte[] value = reader.doFinal(securedValue);
            return value;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
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
        return customPadding(key.getBytes(CHARSET), 32);
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

    private static byte[] customPadding(byte[] key, int key_size) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (key.length % key_size == 0) {
            printArray(key);
            return key;
        }
        int diff = key_size - (key.length % key_size);
//        char c = (char) (diff+ '0');
        byte[] bytes = new byte[key.length + diff];
        System.arraycopy(key,0,bytes,0,key.length);
        for (int i = key.length; i < bytes.length; i++) {
            bytes[i] = (byte) diff;
        }
        printArray(bytes);
        return bytes;
//        StringBuilder sb = new StringBuilder(key);
//        for (int i = 0; i < diff; i++) {
//            sb.append(c);
//        }
//        LogUtils.d("Padding", sb.toString());
//        byte[] bytes = sb.toString().getBytes(CHARSET);
//        printArray(bytes);
//        return sb.toString().getBytes(CHARSET);
    }

    private static void printArray(byte[] bytes) {
//        StringBuilder sb1 = new StringBuilder();
//        sb1.append("[");
//        for (int i = 0; i < bytes.length - 1; i++) {
//            sb1.append(bytes[i]).append(" , ");
//        }
//        sb1.append(bytes[bytes.length - 1]).append(" ]");
//        LogUtils.d("key_bytes", sb1.toString());
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
