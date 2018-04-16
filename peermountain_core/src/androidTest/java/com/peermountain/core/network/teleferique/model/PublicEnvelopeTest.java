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
                String(Base64.decode(TfConstants.KEY_PUBLIC, Base64.DEFAULT))).getEncoded());
        Assert.assertEquals("not same key base64",
                "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUFxUHpRWG1OclBBRGpOMWZOMldNbwpjYUZaZnpTa04vc2ZMZWcvbU5PRHM4VkZaQVJST0VLU0IxK0FNQjlhbXJEdnhXcnoreStTakJqZVBlcmp3ZUYwCm4wZXFwOXdYWnNUSVBjWHpjNkhCTkRCVjlPUEFlT3VEbzhCc1ZaOXUrUnRvZkduY0VPenpLWDU5dnlpSWh1LzIKZEtFSm4yMUFjREo3ZHQ2UnViN3BkOFVBdWY1NzJjUjRocUkxOHh5K2hId3NheVYwcnZNNzFZcTYzdDJweWRmWgo4cnYwWXN0U2xsSjgwVjhHUGRHRjJmYnliZjJzVHdYMk0zdDlOcU9uVytWd1lJSzRvMHYrZTF1TEJzcjQ4dDQ5CkMxTTlBNStVTlNhV1A3ZTd3WkxYY1pCL3VONmQvQytkdERoWGlrMGk0K2o5TnRMeFlvQ2hYc0VMZjg0SzVKOUIKbUlmQnBrdTlLbm95dUtmTVU2cTNaaVYxeCtJVzBpakxNbllXZjBWMXdXbFl4NC9JaWU0cVhIRzZGT1ZIeStnSgo5MXlpZEtRcHQwVnpwYVZ5MjI5NXR3ZUlqUUJQS1l6c3Y2NDhzSlNGcTNTSHZnb3Bad3lOSlNQYmdiTmFsKzBVCjFrNWpFdXpjSmZPRFM0SXIzWVdrNUFBVmw3dmhUUnp0NThod1ZwODYrQnlJUmdvT0pqL3Mzckh0RDBrV2ljUS8KenBIS05EZ2hQNnA3VndlUjU1S2kwc0VScVVPY3RZek4rVzlMY3dOaktPaXBkYW5RajliYmZmelNSN0JOWmVGTQpvUnR2NXRjYnlncEdRaVU4ODZ0QmVLYmVrUm9uS2xTVDFDWVJqSkUyM05mSVNrRCs5TEliYVpJRVR6VFZnbTVJCjF3bmtzVGdZK3crMHdnbmQwVXNmNWdjQ0F3RUFBUT09Ci0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ=="
                , encoded
        );
    }

    @Test
    public void checkRSA() {
//        PublicKey publicKey = SecureHelper.getKey(TfConstants.KEY_PUBLIC);
        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(InstrumentationRegistry.getTargetContext(), TfConstants.KEY_ALIAS);
        String name = SecureHelper.encryptRSAb64(TfConstants.KEY_ALIAS, "test");
        LogUtils.t("name encoded", name);
        String decodedName = SecureHelper.decryptRSAb64(TfConstants.KEY_ALIAS, name);
        LogUtils.t("name decoded", decodedName);
        Assert.assertEquals("not same text", decodedName, "test");
    }

    @Test
    public void checkRSAsign() {
//        PublicKey publicKey = SecureHelper.getKey(TfConstants.KEY_PUBLIC);
        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(InstrumentationRegistry.getTargetContext(), TfConstants.KEY_ALIAS);
        String name = "gqlzaWduYXR1cmXFAgBVeTIfvXNVOpvKQ3IiZex4y6aFGuvpoWn809eEKsz2+7kzuAV5bF6B9bcv2V/F1eyoNrvpD6a/Kz5f6q+GGPE4TF6Npbd03hM8HFPrhMPbiNcgpa2IZXPdp4R5QUTLcxR9N8MYR5ssTM55lEsJVAm7UN5Xub9w500HZ35EcW7fotTaag+ssL5itCghbpxOgEyR8SqSm1CYTqn5h0in751ytBFIW0Gepui0hc2r4nmhQir4H8d6FhdyZJMo4IX6rfEFOAwwlk8HnebI9YIs9oK/qVyIVib6P05GyeW8xe+ejnCqXiAOh0mzozMZCvvKd/wabloHHLJndWyfY9vIWXOU7z0fVbFo9N4X/fdsMFNITkZW/PwIJfHnQNfQwF3c2hpyFHiHmoCyksL3n4xDz3kw31qSKpqRUfst5Pc4I8wyZGftyLBKoVrNqHRTdsnztQJHzvX6fAMddxOuq/1r4PWgZWrO15gnIouyyYTr0LoqTcbcWMaNFVYk+k5VP4cdPirkEmHqIksv6ppC3fxmrlOS2K1dny8PqCYENeFQkWbDzkMKNABHafF+yeFL/XVJR7hQxFPChY6Ythg24d68srtrmmaQMZizoLnkdcr1nWGLT9K0oz55NQicSMzFWJi0ePr3QXZFhqlJ72CoDZudt4p0qFRdaSw2BDDy+hW9xydc7al0aW1lc3RhbXDaA8xncWx6YVdkdVlYUjFjbVhhQXF4V1VTOHpaMDVuU1dGcmNETmhkVFYzYkhGblEyOVhZWEV3V0VGRlVtOUhOM1I2UmtKT01HZEtMMk0yVFdnNVpWSkxSSFZ1UWtZelkyMHlLMnh5YW1sbVVVZENVMHQzU1RkUlVVTTRkaXRpWTBKSWVVbFBPQ3RJVVVORFV6bExWM1ZXZDJweGMzZG9RMkp0UVRsNFRGSmxhelZHVkZWUVZVMTRjbmhsVEV0RU1qZGhNMUJwZDNWMlVFazNRak5oT0d3M2RUTmhiVGhZT0U1MlNrUlJjVTlaUzBKRWJGRTFRM1ZvWWxaVVNESk9ka0ZQVFN0NU1DdHZaSFIxYVdOVFoxWnZka2s0TlVOclUxTjBWREI0YmpSNU16VXJaVEE0UW5SdE1XaHlLMEpvTUhKeGFHOXhkbWcxTUZKell6ZFBSRzlrWjB4clRHTk1SVGwzVFVaVlZHcGpaRVl2VnpCUmEzQnBjV05LVVdWSGRXVXJUVTFHVDJobFdISklaVEJtUXpabk5IcEZkMnczUlcxbFUwVnliR1FyTVdoYVpraDFVVGgxVHpGUFdrMWFPVTVZUlVneFF6QnBkRlZuTlVZd1pWZHNVbEp0UkdSMGVESTFObTFUWnpWMlN6aDNZbTFMY1RNMU5HRlFURGhVTTI1eE5rdFhWekp5ZEVSeFZtSjNRa1J4ZFhkSVdrY3ZLMlIzYXpKMU4wOWtiaXQ1WjA1NVIwd3lha0kwWldGU1ZWUXJWV05MUkc0NU1ubFFjVEpLVVZBeEsweHpjWEZMU0ZGa1RXWk9ORGw0VlU4eFN5dEpSMnhTUkdGbEwwZHllR0p4V2tob2VFTkNSM3B1Y2pCcFJWTkhkVU53YVRsTGR6RnRjbUY1ZFdwNU9GVnJNREJTTVdOQ2NGVnRjMnRHTW1waGNGSjZaVXhMUzNodU5VOXlSRkp5V2prMFZ6bGpNWGh5YlRsWVJuVnlMeTh3UlU5TFkwMVlVMDUwZVRsaVNsY3hWWEJMZEVsbk1rbHhjRU5yTjNCSkt6SkVNVTVQZUV0Q2IyaHpZVE5sZGxFMVVFVmljV2h3TkdkVFNrSnRRMFZMWlRjMGIwWlNWRU5ZU2tjek0xUnlObVJYTDJGa2NXdE5lVGRRYlVSbE1YTnBjSEV4TkVGR1drVkxTMmxWUVdWWGMzUkNRaXRRVWt0SEsybzVhbEJUWWtVck4xWjRNRTEyYnoycGRHbHRaWE4wWVcxd3NqRTFNakk1TWpJeE16VXVNemszTURFNE5BPT0=";
        LogUtils.t("name encoded", name);
        String decodedName = SecureHelper.decryptRSAb64(TfConstants.KEY_ALIAS, name);
        LogUtils.t("name decoded", decodedName);
        Assert.assertEquals("not same text", decodedName, "test");
    }

    @Test
    public void timestamp() throws Exception {
        String time = "gqlzaWduYXR1cmXaAqxiclE5Vi8wQ3d2QmVUTk9UZ1BQR1ZrRm81cHc2LzRjUFc2TUU4QTVMMkRzbHBCRG5QYUk2cVNjUzRTNTFwMjg4NkRBcTRGOWVybWE4a0NyQm11VEJWUEdZMXRidTVveUozVnczQUtOU2hlRzcwbW44VW5VdVBEeFdENUtkdWxQVFpsVDJOOStSbnVzQ3BzSEZyWXlCRWNTbTA3aUtReDU3Y3l0NHhBQlplb2FSem9aZ0NwWW5TdTJoZU9oOExCTzZyUE5GVThId2NpVUNsQ1pTbEJocE1FTkVZMmJjTm81VEpBWmozdDIwd2JDS0Y2ZXF5RnFmMTVMVHkyNHlWbThEVTFlTlVTN0dtdXlmb2thdFZxSjhMZnZVcHdoc25PRzkwYm04dHV2dDBpS1FhTmR3SGZ3YU1RelJBKzF4NGs1dGtXMGZxVkNlbkdmMURWNjZiOUVZUDl5d0tsdTF5eU5FRDljeVZ3bFF1YWVXQUpUTnQ3MGtJU1dGRzZmSG03ZDY5d0tLYlpsNFNqUkNBU2V2bXZldkYrcVdaRVVLd21xRzZVNDF2SngxNmZzYVc4TWd2cmlwY2JLUUdJZ0RpMmo3MXV1SERRNUN1OG9vUDJRdDJTaXZsT281dlBBSFlZakZWYWx0c2VEb1FyMUU1Y1oyaS81dXcva2pTVk9oTkhVNlNuVXkyeUtJWnBPK3NLaHRNc1dka1JvUkRnR3JqbE5MYURkMmlZN0dJM0h3TFJhaXFmYUNvUUZwb1ZmMjlQTVZndFd5RkpNWjgvMDJhYkU2a3BmRmxUTWFEOS9MdzlmYlVxWXBqZ2NZcXdBbnFTRXdLSXFpMzhiWUlMRW5LTCtRRnd4V0RNRUZwTkFqdFFXdENnRWNLc3VzWkU5MnRORkpqU0o0YVR4aDIzbz2pdGltZXN0YW1wsjE1MjI5NDA0NDYuNzcwODI2Ng==";
        String encoded = new String(Base64.decode(time, Base64.DEFAULT));
        Assert.assertNotNull(encoded
        );
    }

}