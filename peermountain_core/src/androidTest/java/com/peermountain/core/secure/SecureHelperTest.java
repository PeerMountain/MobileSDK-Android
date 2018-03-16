package com.peermountain.core.secure;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Galeen on 3/16/2018.
 */
public class SecureHelperTest {
    @Test
    public void hash_hmac() throws Exception {
        String hash = SecureHelper.hash_hmac("test", "pass");
        Log.w("hash",hash);
        Assert.assertNotNull("wrong", hash);
        hash = SecureHelper.hash_hmac("test", "pass");
        Log.w("hash_new",hash);
        Assert.assertNotNull("wrong", hash);
        hash = SecureHelper.hash_hmac_simple("test", "pass");
        Log.w("hash_s",hash);
        Assert.assertNotNull("wrong", hash);
        hash = SecureHelper.hash_hmac_simple("test", "pass");
        Log.w("hash_s_new",hash);
        Assert.assertNotNull("wrong", hash);
    }

    @Test
    public void crypto() throws Exception {
        String encoded = SecureHelper.encodeAES("test", "some information should be hidden");
        Log.w("crypto_encoded",encoded);
        Assert.assertNotNull("wrong", encoded);
        String decoded = SecureHelper.decodeAES("test", encoded);
        Log.w("crypto_decoded",decoded);
        Assert.assertNotNull("wrong", decoded);
    }

    @Test
    public void sign() throws Exception {
        String alias = "test2";
        String data = "some information should be hidden";
        byte[] signature = SecureHelper.sign(alias, data.getBytes());
        if(signature == null){
            Log.w("sign","no key, creating..");
            SecureHelper.createAndroidKeyStoreAsymmetricKey(InstrumentationRegistry.getTargetContext(),alias);
            signature = SecureHelper.sign(alias, data.getBytes());
        }
        if(signature != null){
            Log.w("sign",new String(signature, "UTF-8"));
        }
        Assert.assertNotNull("wrong", signature);
        Assert.assertTrue("wrong signature",SecureHelper.verify(alias, data.getBytes(), signature));
    }

}