package com.peermountain.pm_net.secure;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;

import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1StreamParser;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.Time;
import org.spongycastle.asn1.tsp.MessageImprint;
import org.spongycastle.asn1.tsp.TimeStampReq;
import org.spongycastle.asn1.tsp.TimeStampResp;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;
import org.spongycastle.tsp.TSPException;
import org.spongycastle.tsp.TimeStampRequest;
import org.spongycastle.tsp.TimeStampResponse;
import org.spongycastle.tsp.TimeStampToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

//import org.bouncycastle.asn1.ASN1Boolean;
//import org.bouncycastle.asn1.ASN1EncodableVector;
//import org.bouncycastle.asn1.ASN1Integer;
//import org.bouncycastle.asn1.ASN1ObjectIdentifier;
//import org.bouncycastle.asn1.ASN1Set;
//import org.bouncycastle.asn1.ASN1StreamParser;
//import org.bouncycastle.asn1.cmp.PKIFailureInfo;
//import org.bouncycastle.asn1.cms.Attribute;
//import org.bouncycastle.asn1.cms.AttributeTable;
//import org.bouncycastle.asn1.cms.CMSAttributes;
//import org.bouncycastle.asn1.cms.Time;
//import org.bouncycastle.asn1.tsp.MessageImprint;
//import org.bouncycastle.asn1.tsp.TimeStampReq;
//import org.bouncycastle.asn1.tsp.TimeStampResp;
//import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
//import org.bouncycastle.cms.CMSException;
//import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
//import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
//import org.bouncycastle.tsp.TSPException;
//import org.bouncycastle.tsp.TimeStampRequest;
//import org.bouncycastle.tsp.TimeStampResponse;
//import org.bouncycastle.tsp.TimeStampToken;

/**
 * Created by Galeen on 8/24/2018.
 */
public class TimestampService {
    public static final int base64Flag = Base64.NO_WRAP;
    private static final AlgorithmIdentifier sha512oid = getSha512Oid();
    private static final ASN1ObjectIdentifier baseTsaPolicyId = new ASN1ObjectIdentifier("0.4.0.2023.1.1");

    private SecureRandom random = new SecureRandom();

    private static AlgorithmIdentifier getSha512Oid() {
        DigestAlgorithmIdentifierFinder algorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        return algorithmFinder.find("SHA-512");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public TimestampResponseDto timestamp(byte[] hash, String tsaUrl, String tsaUsername, String tsaPassword,
                                          String tsaPolicyOid) throws IOException {
        MessageImprint imprint = new MessageImprint(sha512oid, hash);

        ASN1ObjectIdentifier tsaPolicyId = !TextUtils.isEmpty(tsaPolicyOid) ? new ASN1ObjectIdentifier(tsaPolicyOid) : baseTsaPolicyId;

        TimeStampReq request = new TimeStampReq(imprint, tsaPolicyId, new ASN1Integer(random.nextLong()),
                ASN1Boolean.TRUE, null);

        byte[] body = request.getEncoded();
        try {
            byte[] responseBytes = getTSAResponse(body, tsaUrl, tsaUsername, tsaPassword);

            ASN1StreamParser asn1Sp = new ASN1StreamParser(responseBytes);
            TimeStampResp tspResp = TimeStampResp.getInstance(asn1Sp.readObject());
            TimeStampResponse tsr = new TimeStampResponse(tspResp);

            checkForErrors(tsaUrl, tsr);

            // validate communication level attributes (RFC 3161 PKIStatus)
            tsr.validate(new TimeStampRequest(request));

            TimeStampToken token = tsr.getTimeStampToken();

            TimestampResponseDto response = new TimestampResponseDto();
            response.setTime(getSigningTime(token.getSignedAttributes()));
            response.setEncodedToken(Base64.encodeToString(token.getEncoded(), base64Flag));

            return response;
        } catch ( TSPException | CMSException  e) {
            throw new IOException(e);
        }
    }

    private void checkForErrors(String tsaUrl, TimeStampResponse tsr) throws IOException {
        PKIFailureInfo failure = tsr.getFailInfo();
        int value = (failure == null) ? 0 : failure.intValue();
        if (value != 0) {
            throw new IOException("Invalid TSA '" + tsaUrl + "' response, code " + value);
        }
    }

    private Calendar getSigningTime(AttributeTable signedAttrTable) throws CMSException {

        ASN1EncodableVector v = signedAttrTable.getAll(CMSAttributes.signingTime);
        switch (v.size()) {
            case 0:
                return null;
            case 1: {
                Attribute t = (Attribute) v.get(0);
                ASN1Set attrValues = t.getAttrValues();
                if (attrValues.size() != 1) {
                    throw new CMSException("A signingTime attribute MUST have a single attribute value");
                }

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(Time.getInstance(attrValues.getObjectAt(0).toASN1Primitive()).getDate());
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                return calendar;
            }
            default:
                throw new CMSException(
                        "The SignedAttributes in a signerInfo MUST NOT include multiple instances of the signingTime attribute");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected byte[] getTSAResponse(byte[] requestBytes, String tsaUrl, String tsaUsername, String tsaPassword) throws IOException {
        URL url = new URL(tsaUrl);
        URLConnection tsaConnection = url.openConnection();
        tsaConnection.setConnectTimeout(5000);
        tsaConnection.setDoInput(true);
        tsaConnection.setDoOutput(true);
        tsaConnection.setUseCaches(false);
        tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary");

        if (!TextUtils.isEmpty(tsaUsername)) {
            String userPassword = tsaUsername + ":" + tsaPassword;
            tsaConnection.setRequestProperty("Authorization", "Basic "
                    + Base64.encodeToString(userPassword.getBytes(),base64Flag));
        }

        OutputStream out = tsaConnection.getOutputStream();
        out.write(requestBytes);
        out.close();

        byte[] respBytes = null;

        try (InputStream input = tsaConnection.getInputStream()) {
            ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = input.read(buffer)) != -1) {
                bufferStream.write(buffer, 0, bytesRead);
            }
//            respBytes = IOUtils.toByteArray(input);
            bufferStream.flush();
            respBytes = bufferStream.toByteArray();
        }

        String encoding = tsaConnection.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("base64")) {
            respBytes = Base64.decode(respBytes,base64Flag);
        }
        return respBytes;
    }

    public static class TimestampResponseDto {

        private String encodedToken;
        private Calendar time;

        public String getEncodedToken() {
            return encodedToken;
        }
        public void setEncodedToken(String encodedToken) {
            this.encodedToken = encodedToken;
        }
        public Calendar getTime() {
            return time;
        }
        public void setTime(Calendar time) {
            this.time = time;
        }
    }
}

