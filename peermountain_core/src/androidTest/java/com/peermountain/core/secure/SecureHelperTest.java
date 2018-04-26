package com.peermountain.core.secure;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;

import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.MessageForSignature;
import com.peermountain.core.network.teleferique.model.PmSignature;
import com.peermountain.core.network.teleferique.model.PublicEnvelope;
import com.peermountain.core.network.teleferique.model.SendObject;
import com.peermountain.core.utils.LogUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Created by Galeen on 3/16/2018.
 */
@RunWith(AndroidJUnit4.class)
public class SecureHelperTest {
    @Test
    public void hash_hmac() throws Exception {
        String hash = SecureHelper.hash_hmac("test", "pass".getBytes());
        LogUtils.t("hash",hash);
        Assert.assertNotNull("wrong", hash);
        hash = SecureHelper.hash_hmac_simple("1234567abcsd".getBytes(), "test".getBytes());
        LogUtils.t("hash_s_new",hash);
        Assert.assertTrue("wrong", hash.equals("X1wCkqKCbHyCOSisMynnHEHs7dtMIfDhacakexeYh3o="));
    }

    @Test
    public void cryptoAES() throws Exception {
        CoderAES.clear();
        String pass = "Test 1";
        String text = "Sample 2";
        String encoded = SecureHelper.encodeAES(pass, text);
        Assert.assertTrue("not same",encoded.equals("y3DwMCeEp5X+sdQChcor8g=="));
        String decoded = SecureHelper.decodeAES(pass, encoded);
        Assert.assertTrue("not same",decoded.equals(text));

        CoderAES.clear();
        pass = "Sample 2";
        text = "Test 1";
        encoded = SecureHelper.encodeAES(pass, text);
        Assert.assertTrue("not same",encoded.equals("u8mKQ1ehL7GvA4DASerD9Q=="));
        decoded = SecureHelper.decodeAES(pass, encoded);
        Assert.assertTrue("not same",decoded.equals(text));

        CoderAES.clear();
        pass = "Chuck 3";
        text = "Sample 3";
        encoded = SecureHelper.encodeAES(pass, text);
        Assert.assertTrue("not same",encoded.equals("HSaNhsXBW+RjSuOQkiMslQ=="));
        decoded = SecureHelper.decodeAES(pass, encoded);
        Assert.assertTrue("not same",decoded.equals(text));
    }

    @Test
    public void cryptoRSA()  {
        String text = "Test 1";
        PublicKey serverPublicKey = SecureHelper.getPublicKey(TfConstants.KEY_PUBLIC_SERVER);
        String encrypted = SecureHelper.encryptRSAb64(text,serverPublicKey);
//        encrypted = SecureHelper.encryptRSAb64(text,serverPublicKey,"RSA/NONE/NoPadding");
//        encrypted = SecureHelper.encryptRSAb64(text,serverPublicKey,"RSA");
        Assert.assertTrue("not same",encrypted.equals("NSHV4njZuQbvwiBSK/gfBMDYmuhVffWtjuz59xnIokMY73ixBzek1fnEkmnChDZYgIwFQDjuaBJ3cvQJmoAQHQSoVjjCDcIdlV0bRLqIQzKv3U5TKXGf8FtJX80myh8F0ClfCzTkBbUZQNUQJEF4VxoZ08Fh+VVtqf+YGkh4ZmqbigVQ/XLkiNU9MMb7j6qidauhcaj9vPMnPCTbA7XSc+hVVXSUjZi+hJYa16pqXUgjuewUyXJXdk49nuza6RVH4lZ0RqNlv92mNosv5MDYCjuIguwAqaYwaR7f862Gogc/tFLDW/S1fXubfwbRQsZN7nxgB/F8s9rWafjr9hLSysZ/ep2IvXbB6p3g87F4GmIjdXq5dRkD/i4J/9VrV3Oii8x9swW7asv7S7/2fJNGozUtfrL9AygYiJrWUihIDnVnJKFbZDi3UugVIoVAOZ3tJHhUd67MO//POaru5PKdNIM2fqUsHmjkA5wGGl1BouswsO1PLjDpxHQkGvpbKzE0jmW88028WNGXiutOYSJIMRHLrJbsdoBb/hBcTYPb8f66XLPBRWIdFEykQRB91R1LfS2fWTHoyW8vqUkKsvAZrPiKLqNzAKNWIn/W3gmIL66gi2a5MITRbh04fq+PmAF8f/n3xxY95rlctcbVvGiJDxEHShm6xe3cGBq6YkcPKL4="));

        text = "Chuck 2";
        encrypted = SecureHelper.encryptRSAb64(text,serverPublicKey);
        Assert.assertTrue("not same",encrypted.equals("Uwq/VsoyScNbSvfzonKDb7+HuB/afGewroGn7fR+smlak6irJr58etbWAdFT56tsmpJstR+nWo9Za0t67mGut0gyyrTuFZ8xX4OktwoMsEqsxUQFX8mmJ4F2tZPaxffw35uLgbNKeda8AUfQDbmWSLBB0ZvWDLezqgaAsKT+lGZ5ogW9kPT1dWLVHS1jdqykUloWj6zopYo7f32SuoKoUAdxPKMRjLxnb8+x0zJ09WTzzRlQKCJ/wrgU+Ujoi34q3cAgUlLIYMz50SnV8hUSayxuAD83Mj7GSHm0C6mYUJl4TkbVjaLakTihke0L3w1NkJ8xwDrUzlgiV+2B8w9QAPIwBG51+YJQmfPQk5UqTMVxwHulih7DfC/OVYHZIfkbSX6e7bZfPypZz91vPgpMelmp9M219vN0CMsZYHVLP59ghfpdJNEhjwajoel2LeHlqW4pAFDjs9S7gBk2ebwQ1tqoHFNev9K57KtusRUbbaTpWHOjCJJRuYhG4ZukRv7cHvGnBwbiKaxu/Kj/Lyi1tX2+TOhvnDB2qiKuZtSciNP4MsVzfWBrgWcblpo/FTdwX2SjyHgwnNFMihXey1JO/pKSzs0pNuiJ7xA+pKKnC0XB1g9noRFh5aYWhsEDQA+fXzBnUpXxdy6ow7EPIAA8Z2N3lv5EGjLOH31mtLN8I38="));

        text = "Sample 3";
        encrypted = SecureHelper.encryptRSAb64(text,serverPublicKey);
        Assert.assertTrue("not same",encrypted.equals("D7UDDcKyaDP07jwW7z43Le3musCVsDcVRjDzEXsg6/BK0kSH/mfmv97AqNDxX9eI025aw1goFJTMQ6ljWAPKaFxOWtl7iQQPzm8htyMivaPHnsXRqFckj40WquIJk1wN64cuYSYSnrqTOx1J37Y54tjlNTF1/kJykvkiVkAbUN2n0G8MmUXodBKrqsVOBCKnlYaNaxV1BKQS5CRhoVwi1cojXL+PMZ54azLpjqoFJ840nBU4OiZ6jBCtozhtnJMm7t5ari7+WNxhwjg2PRwrNMu1n7JWOEEPFUCLydQg8ambhtm8NBBSC6Tm3MOkdQCfvj5kfzQvK6QLGaMm7S8mpxtN4fQRjgYBe6aEaF4n7GrGemQ1drTkcZj3jgtK1Ci0GfbAw1UH4hfoTxse+rEugtQO+06SdzF9MoXePK3p4PbA9hs+3UeCccZPhZqPu9o/j9tXOdgPxqruKHOLBZHW680XFHzBjGud+KbgItEkrt5cNNmWLD2tBi7zsTShpOl6KNjOIHIQVZ3gS08HlmtPYHRf+3c2SZZERHmYM4oGTMYK0eWhAumS3M/aU/kaOwQXr0b1N/2tpk9kiTlasuigoSAsseIq1mu1VbehZ3WjXa9imhwqWkxRp1R8M0C7ZF95Vt2rJc1uVc8YYF7pVVkln6SVv2uG8yeEvlfiWU60bDQ="));
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

    @Test
    public void signature() throws Exception {
        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(InstrumentationRegistry.getTargetContext(),TfConstants.KEY_ALIAS);
        PrivateKey privateKey = keyPair.getPrivate();//SecureHelper.pemPrivateKeyPkcs1OrPkcs8Encoded(TfConstants.KE Y_PRIVATE_TEST);//
        PublicKey publicKey = keyPair.getPublic();//SecureHelper.getPublicKey(TfConstants.KEY_PUBLIC_SERVER);//
        String key = SecureHelper.toPEM(publicKey);
        String time = "gqlzaWduYXR1cmXaAqxCY25QczcwWW80V1BuZ01zMlNBNzFxUmJhWGdBb2c3eDdzS2FITURIRzNhOUZ0OGJZRHZBekZGRnpQQUd3a0pnN1paY3E0U2ozWDJBbVlseDVjNGxrM1JBVngwNkhFSkVGQlVqZ0VwYmFQNUlYcUlMYng1eFAyaVB1NkE1dkFjNUxDTXZDVFVFNFI2Y2hGdnhySWQ4bmhMV0VYMTdSMnlML1NVMTQ1dlNnajh2ejZtYUgrWEwvd292VWM3d0M1T3JsUzdtOFFKNVgxMmp3SzdWWERjUGVzbG9VOHlVZVlta2N0MGlQRVI3NlhGaVpSRnZ2T1RwdWE2bmcxaWRyY1E5cnNrUmxVc1ROdlgrRGJOMk9XVm1oU3JqTjlTK2s0UmxnV2NPakV2U3RjWTFXeU43MVAyYytudXBNVVVLRnAxeVpJOXVSMlhLTVNHeXBTb3hrUitjYzlTTkRUSnNtNFNURE9DVFNuUFZ6NmtSd0dNdmZpWVoxN1FYeEpnOXZTN2d1bWZpaWl0YXBpSkpKTnVOVjlRY09oWlRjS0RLUGtXQnE5RHB0bUdrL203Zlo5UUxGNlRkMjdVYWp1enpIZUxvcmZVVzdOWlpVbk5ySzFLcFBXV0VDS1RQc1ZMSStjRnV5eExZRHl1aFI2dzErUUYyUW5Nb09nSE1QcFJSWGFoeTNhNHNVU2toYnVab05tTm90SGtiM2s4S21UTG1HSTNyMzVBQjdXazgzS1JLMWRrbzk4aDEwNTQyanlTQ0F5YWxZbGpjYlBJU2RFSGpiRGhwT0dOSTdjeXRVS3cwcFRKODFxQlhxWjIyRWFvNW9iNSthQmRQMGFtejRmMWM5TVF6K3dpZFV1WWdzNDJRY0UyVHAvRjRqV3dGWmdoUjY0UTRYd3hKaDh5NE5DRT2pdGltZXN0YW1wsjE1MjI5NDE3MTAuODk0NjEzNQ==";
        String message = "Test";
        String expectedMessageHash = "Uy6qvZV0iA2/drm4zACDLCCm7BE9aCKZVQ16bg80XiU=";

        String messageHash = SecureHelper.sha256AsBase64String(message);
        Assert.assertTrue("wrong hash",messageHash.equals(expectedMessageHash));

        //this is sent to the server
        String resultForServerAsBase64Sign = PublicEnvelope.getMessageSignature(time,messageHash,privateKey);

        //revert
        PmSignature pmSignature = (PmSignature) SecureHelper.read(resultForServerAsBase64Sign, PmSignature.class);
        Assert.assertNotNull("error parsing signature, null value",pmSignature);

        //PmSignature has only byte[] signature and String timestamp, so we crate the expected message inside
        MessageForSignature expectedMessageInSignature = new MessageForSignature(messageHash, pmSignature.getTimestamp());
        byte[] bytesOfExpectedMessageInSignature = SecureHelper.parseMap(expectedMessageInSignature.getAsMap());

        //verify signature
        Assert.assertTrue("wrong signature",
                SecureHelper.verify(bytesOfExpectedMessageInSignature,
                        pmSignature.getSignature(), publicKey));

    }

    @Test
    public void signatureWithServerKey() throws Exception {
        PrivateKey serverPrivateKey = SecureHelper.pemPrivateKeyPkcs1OrPkcs8Encoded(TfConstants.KEY_PRIVATE_TEST);
        String time = "gqlzaWduYXR1cmXaAqxCY25QczcwWW80V1BuZ01zMlNBNzFxUmJhWGdBb2c3eDdzS2FITURIRzNhOUZ0OGJZRHZBekZGRnpQQUd3a0pnN1paY3E0U2ozWDJBbVlseDVjNGxrM1JBVngwNkhFSkVGQlVqZ0VwYmFQNUlYcUlMYng1eFAyaVB1NkE1dkFjNUxDTXZDVFVFNFI2Y2hGdnhySWQ4bmhMV0VYMTdSMnlML1NVMTQ1dlNnajh2ejZtYUgrWEwvd292VWM3d0M1T3JsUzdtOFFKNVgxMmp3SzdWWERjUGVzbG9VOHlVZVlta2N0MGlQRVI3NlhGaVpSRnZ2T1RwdWE2bmcxaWRyY1E5cnNrUmxVc1ROdlgrRGJOMk9XVm1oU3JqTjlTK2s0UmxnV2NPakV2U3RjWTFXeU43MVAyYytudXBNVVVLRnAxeVpJOXVSMlhLTVNHeXBTb3hrUitjYzlTTkRUSnNtNFNURE9DVFNuUFZ6NmtSd0dNdmZpWVoxN1FYeEpnOXZTN2d1bWZpaWl0YXBpSkpKTnVOVjlRY09oWlRjS0RLUGtXQnE5RHB0bUdrL203Zlo5UUxGNlRkMjdVYWp1enpIZUxvcmZVVzdOWlpVbk5ySzFLcFBXV0VDS1RQc1ZMSStjRnV5eExZRHl1aFI2dzErUUYyUW5Nb09nSE1QcFJSWGFoeTNhNHNVU2toYnVab05tTm90SGtiM2s4S21UTG1HSTNyMzVBQjdXazgzS1JLMWRrbzk4aDEwNTQyanlTQ0F5YWxZbGpjYlBJU2RFSGpiRGhwT0dOSTdjeXRVS3cwcFRKODFxQlhxWjIyRWFvNW9iNSthQmRQMGFtejRmMWM5TVF6K3dpZFV1WWdzNDJRY0UyVHAvRjRqV3dGWmdoUjY0UTRYd3hKaDh5NE5DRT2pdGltZXN0YW1wsjE1MjI5NDE3MTAuODk0NjEzNQ==";
        String data = "Test";
        String expectedDataHash = "Uy6qvZV0iA2/drm4zACDLCCm7BE9aCKZVQ16bg80XiU=";

        String hash = SecureHelper.sha256AsBase64String(data);
        Assert.assertTrue("wrong hash",hash.equals(expectedDataHash));

        String resultBase64Sign = PublicEnvelope.getMessageSignature(time,hash,serverPrivateKey);
        Assert.assertTrue("wrong signature", resultBase64Sign.equals("gqlzaWduYXR1cmXFAgAqcs2XIJ/I4m0CR4Wzun6EkvtfMBH9hDJTJCKn9Pg08HGaYJKT5F13/uC9gKA0RZxdqzkL03pdstHzcavRSzdkzpiVQpvvc2L+LZJRK3qTl87aTwDRvFKgucmHMsxaRx4nmOwGexMcDtrQb5RHjc1HLdZ2z3/OTJSkpRA9Ef+8rvCLsy56gncT50B8t0vnI8DfFHTdlIPgRI37uysxYwh4ayB3Tdmg1/bnPle+lmRFxuaVR8CPRYAA9FkinAaT2nEUw9holPa0F20UoUfnbTOMRWm3HkXmLirnjrW1nzuOXmytQ0G1ZaJN1MICdVHcGo2SrplOwW132kN/MxwGcSnEUoc5TAwELhd8zz/7YbcbPN+AqA6Ka2SHExHN32Za6lWqXikiKTAK1AsmX4+d/jlHsaDsyjd0QbTAw2GTYju6A60rfN2XyqEf5ITo3PpNRs4gwibjh79ExfKoUp5/0YzvDK4INLqrVXH0qIlgUg5qMK9HNjQXRqk5+Qbsb3dz2sO4wnJ2sWtg0Kb3DKbFw2r0lZA8/oeOpmwQxEUoe8pfWkm2dr8eZcBjkvrCss3WrFWwqMAEk3bGUmnw+Mi+/rkB6qxejdKqYeTTWYGoP7864BBwFLlNv+qo426dSoGdqsu169G6ndATXdOHQf/AwquqlKbKZteIkm/h3IPExCK39al0aW1lc3RhbXDaA8xncWx6YVdkdVlYUjFjbVhhQXF4Q1kyNVFjemN3V1c4MFYxQnVaMDF6TWxOQk56RnhVbUpoV0dkQmIyYzNlRGR6UzJGSVRVUklSek5oT1VaME9HSlpSSFpCZWtaR1JucFFRVWQzYTBwbk4xcGFZM0UwVTJveldESkJiVmxzZURWak5HeHJNMUpCVm5nd05raEZTa1ZHUWxWcVowVndZbUZRTlVsWWNVbE1ZbmcxZUZBeWFWQjFOa0UxZGtGak5VeERUWFpEVkZWRk5GSTJZMmhHZG5oeVNXUTRibWhNVjBWWU1UZFNNbmxNTDFOVk1UUTFkbE5uYWpoMmVqWnRZVWdyV0V3dmQyOTJWV00zZDBNMVQzSnNVemR0T0ZGS05WZ3hNbXAzU3pkV1dFUmpVR1Z6Ykc5Vk9IbFZaVmx0YTJOME1HbFFSVkkzTmxoR2FWcFNSbloyVDFSd2RXRTJibWN4YVdSeVkxRTVjbk5yVW14VmMxUk9kbGdyUkdKT01rOVhWbTFvVTNKcVRqbFRLMnMwVW14blYyTlBha1YyVTNSaldURlhlVTQzTVZBeVl5dHVkWEJOVlZWTFJuQXhlVnBKT1hWU01saExUVk5IZVhCVGIzaHJVaXRqWXpsVFRrUlVTbk50TkZOVVJFOURWRk51VUZaNk5tdFNkMGROZG1acFdWb3hOMUZZZUVwbk9YWlROMmQxYldacGFXbDBZWEJwU2twS1RuVk9WamxSWTA5b1dsUmpTMFJMVUd0WFFuRTVSSEIwYlVkckwyMDNabG81VVV4R05sUmtNamRWWVdwMWVucElaVXh2Y21aVlZ6ZE9XbHBWYms1eVN6RkxjRkJYVjBWRFMxUlFjMVpNU1N0alJuVjVlRXhaUkhsMWFGSTJkekVyVVVZeVVXNU5iMDluU0UxUWNGSlNXR0ZvZVROaE5ITlZVMnRvWW5WYWIwNXRUbTkwU0d0aU0yczRTMjFVVEcxSFNUTnlNelZCUWpkWGF6Z3pTMUpMTVdScmJ6azRhREV3TlRReWFubFRRMEY1WVd4WmJHcGpZbEJKVTJSRlNHcGlSR2h3VDBkT1NUZGplWFJWUzNjd2NGUktPREZ4UWxoeFdqSXlSV0Z2Tlc5aU5TdGhRbVJRTUdGdGVqUm1NV001VFZGNkszZHBaRlYxV1dkek5ESlJZMFV5VkhBdlJqUnFWM2RHV21kb1VqWTBVVFJZZDNoS2FEaDVORTVEUlQycGRHbHRaWE4wWVcxd3NqRTFNakk1TkRFM01UQXVPRGswTmpFek5RPT0="));

    }

    @Test
    public void parse()  {
        SendObject sendObject = new SendObject().setQuery("some query").setVariables("some variables");
        String msgpacked = Base64.encodeToString(SecureHelper.parse(sendObject),Base64.DEFAULT);
        Log.w("msgpacked",msgpacked);
        Assert.assertNotNull("wrong", msgpacked);
        Map<String, Object> sendObject1 = SecureHelper.read(msgpacked);
        Assert.assertEquals("not same objects",sendObject.getQuery(),sendObject1.get("query"));
        Assert.assertEquals("not same objects",sendObject.getVariables(),sendObject1.get("variables"));
    }

}