package com.peermountain.core.network.teleferique.model;

import android.support.test.InstrumentationRegistry;
import android.util.Base64;

import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

import junit.framework.Assert;

import org.junit.Test;

import java.security.KeyPair;

/**
 * Created by Galeen on 3/20/2018.
 */
public class PublicEnvelopeTest {
    static final String publicKey = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUF2aWJzNlFKMjNEdFUwMW1MVm82RgpCOWV5ajEyRnBQSHZnRnZRMzl6ZFJGblozanhxdkZ4RU5XQkZyQlY0eDExZW5oNFUzZGpCZzJRaFl1aUVWWWxmClR0bzlORUdRdFJ6NWc1a2FNM3lpWk1WWElreVZtZFh2VTBjU3NRcVFQMDBsdDJ0bTR6ZENseFZ2d3Qzb04yS24KeExINmFPL0VOdzY0ZnA0cnFTcTh6SmNqWU5CR2RWakZTTmtXajd3eGVPckdnVW9ja0lEeEdtbGZjYkYvWVJ4TApQckpiRWVyeDZDbGtSd1BWbGdvZjhMdnMydWFFY1B1TzBQT0MzUjMrc01WRTRkNjI3dEFsNktSMmVXLzk4UmRuCkkyYlFZY1V6SzlMOS9YM2xVMjhMNXNVSlFCcXRzb0VjRWJPWXh5bEFFa0JtOWpQbjcxZlYyMjQ1b0ticzZZQm0KaFJOeCtsbnc5RHVnTHJCNFQyWXp1KzNKTlI1Rk5YRCtTa1c4QXkxdmNQbUFlTUVBc3ZIb1hOVXhWSnpkNWh3RApGSU1yRHVVdWlQN2pGK1BOaDRTR2FVZ1VJZ2JrMzZycmdNUDh6MHhybmJFTmg5L3VIaEJTYWhSSGI3YTNEQXdZCmR3TWRrNUFabTNsR1dMOStJK1lQRkVIcFNZNnp5M3k5Wk54Y3BxMkxEdkVSTU1XNk5xSHVlOHRQSUk2dXRUNk4KMUV4R24yTzZwaTdSUUVzN1p2SzRNcGV5czVaU3NmY25GYlJNck5WYllCcStidFVZdzEvRlAvUC9ZR0o3Q1FIbApJRDZ5dFlkck9EUEJmdEF2NGUxYXZtcUNpdCs3TVp5Sk1FMnp4Rzcxa0JKYTU5cWN2UVhmM0FvWnhmajB0bkhHCm9ubXdDalJ2YTlYbWd1RE9STkw0NjBzQ0F3RUFBUT09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ==";

    @Test
    public void getAddress() throws Exception {
        checkKey(publicKey);
    }

    private void checkKey(String publicKey) {
        String address = PublicEnvelope.getAddress(SecureHelper.getKey(publicKey).getEncoded());
        LogUtils.t(address);
        Assert.assertEquals("not same addresses", address, "2n8QM67YsCZUyWDp6muaDMtvjkg3gU6EFJL");
    }

    @Test
    public void decodeKey() throws Exception {
        String encoded = SecureHelper.toBase64String(SecureHelper.getKey(new
                String(Base64.decode(TfConstants.KEY_PUBLIC,Base64.DEFAULT))).getEncoded());
        Assert.assertEquals("not same key base64",
                "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUFxUHpRWG1OclBBRGpOMWZOMldNbwpjYUZaZnpTa04vc2ZMZWcvbU5PRHM4VkZaQVJST0VLU0IxK0FNQjlhbXJEdnhXcnoreStTakJqZVBlcmp3ZUYwCm4wZXFwOXdYWnNUSVBjWHpjNkhCTkRCVjlPUEFlT3VEbzhCc1ZaOXUrUnRvZkduY0VPenpLWDU5dnlpSWh1LzIKZEtFSm4yMUFjREo3ZHQ2UnViN3BkOFVBdWY1NzJjUjRocUkxOHh5K2hId3NheVYwcnZNNzFZcTYzdDJweWRmWgo4cnYwWXN0U2xsSjgwVjhHUGRHRjJmYnliZjJzVHdYMk0zdDlOcU9uVytWd1lJSzRvMHYrZTF1TEJzcjQ4dDQ5CkMxTTlBNStVTlNhV1A3ZTd3WkxYY1pCL3VONmQvQytkdERoWGlrMGk0K2o5TnRMeFlvQ2hYc0VMZjg0SzVKOUIKbUlmQnBrdTlLbm95dUtmTVU2cTNaaVYxeCtJVzBpakxNbllXZjBWMXdXbFl4NC9JaWU0cVhIRzZGT1ZIeStnSgo5MXlpZEtRcHQwVnpwYVZ5MjI5NXR3ZUlqUUJQS1l6c3Y2NDhzSlNGcTNTSHZnb3Bad3lOSlNQYmdiTmFsKzBVCjFrNWpFdXpjSmZPRFM0SXIzWVdrNUFBVmw3dmhUUnp0NThod1ZwODYrQnlJUmdvT0pqL3Mzckh0RDBrV2ljUS8KenBIS05EZ2hQNnA3VndlUjU1S2kwc0VScVVPY3RZek4rVzlMY3dOaktPaXBkYW5RajliYmZmelNSN0JOWmVGTQpvUnR2NXRjYnlncEdRaVU4ODZ0QmVLYmVrUm9uS2xTVDFDWVJqSkUyM05mSVNrRCs5TEliYVpJRVR6VFZnbTVJCjF3bmtzVGdZK3crMHdnbmQwVXNmNWdjQ0F3RUFBUT09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ=="
                , encoded
        );
    }

    @Test
    public void checkRSA() {
//        PublicKey publicKey = SecureHelper.getKey(TfConstants.KEY_PUBLIC);
        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(InstrumentationRegistry.getTargetContext(),TfConstants.KEY_ALIAS);
        String name = SecureHelper.encryptRSAb64(TfConstants.KEY_ALIAS,"test");
        LogUtils.t("name encoded", name);
        String decodedName =  SecureHelper.decryptRSAb64(TfConstants.KEY_ALIAS,name);
        LogUtils.t("name decoded",decodedName);
        Assert.assertEquals("not same text", decodedName, "test");
    }

}