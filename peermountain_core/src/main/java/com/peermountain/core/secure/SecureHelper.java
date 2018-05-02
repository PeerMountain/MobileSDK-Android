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
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERInteger;
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
import java.security.GeneralSecurityException;
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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
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
    private static final String RSA_TRANSFORMATION_ENCRYPT = "RSA/ECB/PKCS1Padding";//for python/server
    private static final int KEY_SIZE = 4096;
    public static final int base64Flag = Base64.NO_WRAP;

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

//        if (Build.VERSION.SDK_INT > 18) {
//            builder.setKeySize(KEY_SIZE);
//        }

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

    public static byte[] sign(byte[] data, PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(key);
        s.update(data);
        return s.sign();//signature
    }

    public static boolean verify(String alias, byte[] data, byte[] signature) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null || keyPair.getPublic() == null) return false;
        return verify(data, signature, keyPair.getPublic());
    }

    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(publicKey);
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

    public static String encryptRSAb64(String alias, String value) {
        byte[] bytes =  encryptRSA(alias, value);
        return Base64.encodeToString(bytes, base64Flag);
    }

    public static String encryptRSAb64(String value, PublicKey publicKey) {
        byte[] bytes =  encryptRSA(value, publicKey);
        return Base64.encodeToString(bytes, base64Flag);
    }

    public static String encryptRSAb64(String value, PublicKey publicKey, String transformation) {
        byte[] bytes =  encryptRSA(value, publicKey,transformation);
        return Base64.encodeToString(bytes, base64Flag);
    }

    public static byte[] encryptRSA(String alias, String value) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null || keyPair.getPublic() == null) return null;
        return encryptRSA(value,keyPair.getPublic());
    }

    public static byte[] encryptRSA(String value, PublicKey publicKey) {
        return encryptRSA(value, publicKey, RSA_TRANSFORMATION_ENCRYPT);
    }

    public static byte[] encryptRSA(String value, PublicKey publicKey, String transformation) {
        if (publicKey == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return encryptedBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptRSAb64(String value, PrivateKey privateKey) {
        byte[] bytes =  decryptRSA(Base64.decode(value, base64Flag),privateKey);
        return new String(bytes);
    }

    public static String decryptRSAb64(String alias, String value) {
        byte[] bytes =  decryptRSA(alias, Base64.decode(value, base64Flag));
        return new String(bytes);
    }

    public static byte[] decryptRSA(String alias,String value) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null) return null;
        return decryptRSA(value.getBytes(),keyPair.getPrivate());
    }

    public static byte[] decryptRSA(String alias,byte[] value) {
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair(alias);
        if (keyPair == null) return null;
        return decryptRSA(value,keyPair.getPrivate());
    }

    public static byte[] decryptRSA(byte[] value, PrivateKey privateKey) {
        if (privateKey == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(value);
            return decryptedBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
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

            return parseMap(objectAsMap);
//            return Base64.encodeToString(bytes, base64Flag);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
//        return new Gson().toJson(data);
    }

    public static byte[] parseMap(Map<String, Object> objectAsMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] bytes = objectMapper.writeValueAsBytes(objectAsMap);
        return bytes;
    }

    public static byte[] parseLinkedMap(LinkedHashMap<String, Object> objectAsMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] bytes = objectMapper.writeValueAsBytes(objectAsMap);
        return bytes;
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

    public static byte[] fromBase64(String s)  {
        return Base64.decode(s, base64Flag);
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

    /**
     *
     * @param secret to use
     * @param value to hash
     * @return hmac hash as base64String
     */
    public static String hash_hmac_simple(byte[] secret, byte[] value) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey = new SecretKeySpec(secret, "HmacSHA256");
            sha256_HMAC.init(secretKey);
            String hash = Base64.encodeToString(sha256_HMAC.doFinal(value), base64Flag);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String hash_hmac(String password, byte[] bytes) {
        return hash_hmacAsHex(password, bytes, generateSalt(40));
    }

    public static String hash_hmacAsHex(String password, byte[] bytes,byte[] salt){
        return bin2hex(hash_hmac(password, bytes, salt));
    }

    public static String hash_hmacAsB64(String password, byte[] bytes,byte[] salt){
        return toBase64String(hash_hmac(password, bytes, salt));
    }

    public static byte[] hash_hmac(String password, byte[] bytes,byte[] salt) {

   /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
//        int saltLength = 40; // bytes; should be the same size as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
//        byte[] salt; // Should be of saltLength

   /* When first creating the key, obtain a salt with this: */
//        salt = generateSalt(saltLength);

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
            return sha256_HMAC.doFinal(bytes);
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

    public static byte[] generateSalt(int saltLength) {
        byte[] salt;SecureRandom random = new SecureRandom();
        salt = new byte[saltLength];
        random.nextBytes(salt);
        return salt;
    }


    public static PublicKey getPublicKey(String key) {
        try {
            if (key.contains("-----BEGIN PUBLIC KEY-----") || key.contains("-----END PUBLIC KEY-----"))
                key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
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

    public static String toPEM(PrivateKey privateKey) {
        return "-----BEGIN RSA PRIVATE KEY-----\n" +
                Base64.encodeToString(privateKey.getEncoded(),Base64.NO_WRAP) +
                "\n-----END RSA PRIVATE KEY-----\n";
    }

    public static PrivateKey pemPrivateKeyPkcs1OrPkcs8Encoded(String privateKeyPem) throws GeneralSecurityException {
        // PKCS#8 format
        final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
        final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

        // PKCS#1 format
        final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
        final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

//        Path path = Paths.get(pemFileName.getAbsolutePath());
//
//        String privateKeyPem = new String(Files.readAllBytes(path));

        if (privateKeyPem.contains(PEM_PRIVATE_START)) { // PKCS#8 format
            privateKeyPem = privateKeyPem.replace(PEM_PRIVATE_START, "").replace(PEM_PRIVATE_END, "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");

            byte[] pkcs8EncodedKey = Base64.decode(privateKeyPem, Base64.DEFAULT);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));

        } else if (privateKeyPem.contains(PEM_RSA_PRIVATE_START)) {  // PKCS#1 format

            privateKeyPem = privateKeyPem.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");

            byte[] encodedPrivateKey = Base64.decode(privateKeyPem, Base64.DEFAULT);

            try {
                ASN1Sequence primitive = (ASN1Sequence) ASN1Sequence
                        .fromByteArray(encodedPrivateKey);
                Enumeration<?> e = primitive.getObjects();
                BigInteger v = ((DERInteger) e.nextElement()).getValue();

                int version = v.intValue();
                if (version != 0 && version != 1) {
                    throw new IllegalArgumentException("wrong version for RSA private key");
                }
                /**
                 * In fact only modulus and private exponent are in use.
                 */
                BigInteger modulus = ((DERInteger) e.nextElement()).getValue();
                BigInteger publicExponent = ((DERInteger) e.nextElement()).getValue();
                BigInteger privateExponent = ((DERInteger) e.nextElement()).getValue();
                BigInteger prime1 = ((DERInteger) e.nextElement()).getValue();
                BigInteger prime2 = ((DERInteger) e.nextElement()).getValue();
                BigInteger exponent1 = ((DERInteger) e.nextElement()).getValue();
                BigInteger exponent2 = ((DERInteger) e.nextElement()).getValue();
                BigInteger coefficient = ((DERInteger) e.nextElement()).getValue();

                RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PrivateKey pk = kf.generatePrivate(spec);
                return pk;
            } catch (IOException e2) {
                throw new IllegalStateException();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            } catch (InvalidKeySpecException e) {
                throw new IllegalStateException(e);
            }
        }

        throw new GeneralSecurityException("Not supported format of a private key");
    }
}
