package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Galeen on 3/20/2018.
 */
public class PublicEnvelopeTest {
    static final String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAz614m40n+FfHIzLNFKaR\n" +
            "    14ownMR6JAmtZ2UV5XUCfhrQxStGnVkwIKxOsg3ZgCsjbHRfMx2NDlubk7jmj7qh\n" +
            "    Jy5YRuAViWke6dnJ6CbE6W2ErUXwlqbpWwFRaeLof/4Hb+PhwpXYBzBBERAk8rrC\n" +
            "    /yN8kYqvMUBd1mi6w+8StLkqvg5MRnx/g5/yF+lvGOeHfRMox2MtUD7IM6Z5Z4ym\n" +
            "    axNe3faOCl8oBTKypLezlM+phQ0Uk4uMejA6YoSFv+f5pf4JJnx6DMzSWSvo4GPX\n" +
            "    /OYKTfmSn8XNT5eCYmhwzF3vRTw+AffR4JHLTk23ER4uJpaw99Iiqo4yDbJNYgro\n" +
            "    dXMvGhYh6OoFDovFXUbcFzP52dg5hmoMYn9eZLwBKIAMcSMPNxJks38kZmr/hHCc\n" +
            "    9NLZbHRkoJ9dn2nRwD4YxRuV03cIsL+KDbn0u3uTH9aExkxEQ44IHsAnHlV5NxDb\n" +
            "    JHF0xMcFYoJOouKDFaD4FUcYtdQ2VheuFOEfM9aVutOKnTacmLHnkHmg6wH/5GhP\n" +
            "    zAWYWD376SyKKPqNcKFomvONIkNKiCX9HBtIUZl68skpihdocPWEkOPCwcAhZNmp\n" +
            "    P6YsepN15X/tAf67x/ssZ7ktACa2Kc9rSVA4NxWBvmxrnQ5UlVPzfSqWlcDtVVnP\n" +
            "    +xZeGuS3KJx307sqM0lCYf0CAwEAAQ==";

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
        String encoded = SecureHelper.toBase64String(SecureHelper.getKey(publicKey).getEncoded());
        Assert.assertEquals("not same key base64",
                "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUFxUHpRWG1OclBBRGpOMWZOMldNbwpjYUZaZnpTa04vc2ZMZWcvbU5PRHM4VkZaQVJST0VLU0IxK0FNQjlhbXJEdnhXcnoreStTakJqZVBlcmp3ZUYwCm4wZXFwOXdYWnNUSVBjWHpjNkhCTkRCVjlPUEFlT3VEbzhCc1ZaOXUrUnRvZkduY0VPenpLWDU5dnlpSWh1LzIKZEtFSm4yMUFjREo3ZHQ2UnViN3BkOFVBdWY1NzJjUjRocUkxOHh5K2hId3NheVYwcnZNNzFZcTYzdDJweWRmWgo4cnYwWXN0U2xsSjgwVjhHUGRHRjJmYnliZjJzVHdYMk0zdDlOcU9uVytWd1lJSzRvMHYrZTF1TEJzcjQ4dDQ5CkMxTTlBNStVTlNhV1A3ZTd3WkxYY1pCL3VONmQvQytkdERoWGlrMGk0K2o5TnRMeFlvQ2hYc0VMZjg0SzVKOUIKbUlmQnBrdTlLbm95dUtmTVU2cTNaaVYxeCtJVzBpakxNbllXZjBWMXdXbFl4NC9JaWU0cVhIRzZGT1ZIeStnSgo5MXlpZEtRcHQwVnpwYVZ5MjI5NXR3ZUlqUUJQS1l6c3Y2NDhzSlNGcTNTSHZnb3Bad3lOSlNQYmdiTmFsKzBVCjFrNWpFdXpjSmZPRFM0SXIzWVdrNUFBVmw3dmhUUnp0NThod1ZwODYrQnlJUmdvT0pqL3Mzckh0RDBrV2ljUS8KenBIS05EZ2hQNnA3VndlUjU1S2kwc0VScVVPY3RZek4rVzlMY3dOaktPaXBkYW5RajliYmZmelNSN0JOWmVGTQpvUnR2NXRjYnlncEdRaVU4ODZ0QmVLYmVrUm9uS2xTVDFDWVJqSkUyM05mSVNrRCs5TEliYVpJRVR6VFZnbTVJCjF3bmtzVGdZK3crMHdnbmQwVXNmNWdjQ0F3RUFBUT09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ=="
                , encoded
        );
    }
}