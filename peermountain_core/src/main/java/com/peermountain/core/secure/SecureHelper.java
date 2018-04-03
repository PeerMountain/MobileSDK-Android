package com.peermountain.core.secure;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Galeen on 3/16/2018.
 */

public class SecureHelper {
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 4096;
    private static final int base64Flag = Base64.NO_WRAP;

    private static KeyStore createAndroidKeyStore() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    public static KeyPair createAndroidKeyStoreAsymmetricKey(Context context, String alias) {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initGeneratorWithKeyGenParameterSpec(generator, alias);
            } else {
                initGeneratorWithKeyPairGeneratorSpec(context, generator, alias);
            }
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
        // Generates Key with given spec and saves it to the KeyStore
        return generator.generateKeyPair();
    }

    public static KeyPair getOrCreateAndroidKeyStoreAsymmetricKey(Context context, String alias) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null) {
            keyPair = createAndroidKeyStoreAsymmetricKey(context, alias);
        }
        return keyPair;
    }


    private static void initGeneratorWithKeyPairGeneratorSpec(Context context, KeyPairGenerator generator, String alias) throws InvalidAlgorithmParameterException {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 20);

        KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN=${alias} CA Certificate"))
                .setStartDate(startDate.getTime())
                .setEndDate(endDate.getTime());

        if (Build.VERSION.SDK_INT > 18) {
            builder.setKeySize(KEY_SIZE);
        }

        generator.initialize(builder.build());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void initGeneratorWithKeyGenParameterSpec(KeyPairGenerator generator, String alias) throws InvalidAlgorithmParameterException {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN
                | KeyProperties.PURPOSE_VERIFY)
                .setUserAuthenticationRequired(false)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(KEY_SIZE);
        generator.initialize(builder.build());
    }

    public static KeyPair getAndroidKeyStoreAsymmetricKeyPair(String alias) {
        KeyStore keyStore = createAndroidKeyStore();
        if (keyStore == null) return null;
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, null);
            publicKey = keyStore.getCertificate(alias) != null ? keyStore.getCertificate(alias).getPublicKey() : null;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        if (privateKey != null && publicKey != null) {
            return new KeyPair(publicKey, privateKey);
        } else {
            return null;
        }
    }

    public static byte[] sign(String alias, byte[] data) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null) return null;
        try {
            return sign(data, keyPair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] sign(byte[] data, PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(key);
        s.update(data);
        return s.sign();//signature
    }

    public static boolean verify(String alias, byte[] data, byte[] signature) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null) return false;
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(keyPair.getPublic());
            s.update(data);
            return s.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteKeyPair(String alias) {
        KeyStore keyStore = createAndroidKeyStore();
        if (keyStore == null) return;
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the object to Map and packed it for the server
     *
     * @param data object to pack
     * @return Base64String
     */
    public static byte[] parse(Object data) {
//        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> objectAsMap = objectMapper.convertValue(data, Map.class);

            byte[] bytes = objectMapper.writeValueAsBytes(objectAsMap);
            return bytes;
//            return Base64.encodeToString(bytes, base64Flag);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
//        return new Gson().toJson(data);
    }

    public static String parseToBase64(Object data) {
        return Base64.encodeToString(parse(data), base64Flag);
    }

    public static Object read(String parsedData, Class classType) {
        try {
            return read(parsedData.getBytes(CoderAES.CHARSET), classType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        return new Gson().toJson(data);
        return null;
    }

    public static Object read(byte[] parsedData, Class classType) {
//        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        try {
            byte[] bytes;
            try {
                bytes = Base64.decode(parsedData, base64Flag);
            } catch (Exception e) {
                e.printStackTrace();
                bytes = parsedData;
            }
            return objectMapper.readValue(bytes, classType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return new Gson().toJson(data);
    }

    public static Map<String, Object> read(String parsedData) {
        try {
            return read(parsedData.getBytes(CoderAES.CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
//        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
//        try {
//            byte[] bytes = parsedData.getBytes(CoderAES.CHARSET);//Base64.decode(parsedData, base64Flag);
//            return objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
//        return new Gson().toJson(data);
    }

    public static Map<String, Object> read(byte[] parsedData) {
//        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        try {
            //byte[] bytes = parsedData.getBytes(CoderAES.CHARSET);//Base64.decode(parsedData, base64Flag);
            return objectMapper.readValue(parsedData, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return new Gson().toJson(data);
    }

    /**
     * Read the object from Base64 string.
     */
    public static Object fromBase64String(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.decode(s, base64Flag);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string.
     */
    public static String toBase64String(byte[] bytes) {
        return Base64.encodeToString(bytes, base64Flag);
    }

    /**
     * Write the object to a Base64 string.
     */
    public static String toBase64String(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), base64Flag);
    }

    /**
     * Write the object to a HEX string.
     */
    public static String toHexString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return bin2hex(baos.toByteArray());
//        return Base64.encodeToString(baos.toByteArray(), base64Flag);
    }

    /**
     * Write the object to a byte[].
     */
    public static byte[] toBytes(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return baos.toByteArray();
    }

    public static byte[] ripemd160(String password) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        byte[] bytes = password.getBytes();
        digest.update(bytes, 0, bytes.length);
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] sha256(String password) {
        return sha256(password.getBytes());
    }

    public static byte[] sha256(byte[] bytes) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        }
        digest.reset();
        return digest.digest(bytes);
    }

    /**
     * @param data bytes
     * @return HEX String
     */
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static String sha256AsBase64String(String password) {
        return sha256AsBase64String(password.getBytes());
    }

    public static String sha256AsBase64String(byte[] data) {
        return Base64.encodeToString(sha256(data), base64Flag);
    }

    public static String sha256AsHexString(String password) {
        return bin2hex(sha256(password));
    }

    public static String encodeAES(String pass, String value) {
        return CoderAES.encrypt(pass, value);
    }

    public static String encodeAES(String pass, byte[] value) {
        return CoderAES.encrypt(pass, value);
    }

    /**
     * use this method only to visualize data as String
     * if you have to convert this data to object as SecureHelper.read
     * use decodeAES(String pass, byte[] value) and convert the response to String
     * @param pass
     * @param value
     * @return
     */
    public static String decodeAES(String pass, String value) {
        return CoderAES.decrypt(pass, value);
    }

    public static byte[] decodeAES(String pass, byte[] value) {
        return CoderAES.decrypt(pass, value);
    }

    public static String hash_hmac_simple(String secret, String str) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        String hash = Base64.encodeToString(sha256_HMAC.doFinal(str.getBytes()), base64Flag);
        return hash;
    }

    public static String hash_hmac(String password, byte[] bytes) {

   /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
        int saltLength = 40; // bytes; should be the same size as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
        byte[] salt; // Should be of saltLength

   /* When first creating the key, obtain a salt with this: */
        SecureRandom random = new SecureRandom();
        salt = new byte[saltLength];
        random.nextBytes(salt);

   /* Use this to derive the key from the password: */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                iterationCount, keyLength);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256");

            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(key);
            return bin2hex(sha256_HMAC.doFinal(bytes));
//            return Base64.encodeToString(sha256_HMAC.doFinal(str.getBytes()), base64Flag);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static PublicKey getKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key.getBytes(), base64Flag);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toPEM(PublicKey publicKey) {
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        try {
            pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
            pemWriter.flush();
            pemWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
