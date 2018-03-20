package com.peermountain.core.network.teleferique.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.secure.Base58;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

import java.io.IOException;
import java.security.KeyPair;

/**
 * Created by Galeen on 3/14/2018.
 */

public class PublicEnvelope {
    public String messageHash; // sha256
    public String messageType; // typecode , from TfConstants
    public String dossierHash; // hmac-sha256
    public String bodyHash; //sha256
    public String messageSig; // RSA signature
    public String message; // (enc)BLOB - AESEncryptedBlob!
    public String sender; // PM Address

    public PublicEnvelope() {
    }

    public PublicEnvelope(Invitation invitation) {
        String pass = "Peer Mountain";
        try {
            //AES encrypt body with passphrase='Peer Mountain'
            invitation.setInviteName(SecureHelper.encodeAES(pass,invitation.getInviteName()));

            MessageBody messageBody = getMessage(invitation);
//            LogUtils.d("full messageBody", messageBody);

            //AES encrypt body with passphrase='Peer Mountain'
            String encryptedBody = SecureHelper.encodeAES(pass,messageBody.getMessageBody());
            message = encryptedBody;
//            LogUtils.d("full messageBody encrypted", message);

            messageHash = SecureHelper.sha256AsBase64String(encryptedBody);
//            LogUtils.d("encrypted messageHash", messageHash);

            bodyHash = messageBody.getBodyHash();//SecureHelper.sha256AsBase64String(messageBody);
//            LogUtils.d("full bodyHash", bodyHash);

            messageType = "REGISTRATION";
//            LogUtils.d("messageType", messageType+"");

            dossierHash = SecureHelper.hash_hmac(pass,messageBody.getMessageBody());
//            LogUtils.d("dossierHash", dossierHash);

            messageSig = getMessageSignature();
//            LogUtils.d("messageSig", messageSig);


            KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(PeerMountainManager.getApplicationContext(),TfConstants.KEY_ALIAS);
            sender = getAddress(keyPair.getPublic().getEncoded());

//            LogUtils.d("sender", sender);

            LogUtils.d("envelope", new Gson().toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// The public key of the pair is hashed SHA-256.
//    step_1 = SHA256.new(self.key.publickey().exportKey()).digest()
//        # The resulting Hash is further hashed with RIPEMD-160.
//    step_2 = RIPEMD.new(step_1).digest()
//        # Two bytes are prefixed to the resulting RIPEMD-160 hash in order to
//        # identify the deployment system.
//    step_3 = bytes(self.prefix) + step_2
//        # A checksum is calculated by SHA-256 hashing the extended RIPEMD-160 hash, then hashing
//        # the resulting hash once more.
//            step_4_checksum = SHA256.new(SHA256.new(step_3).digest()).digest()
//        # The last 4 bytes of the final hash are added as the
//        # trailing 4 bytes of the extended RIPEMD-160 hash. This is the
//        # checksum
//            step_4 = step_3 + step_4_checksum[4:]
//            # The resulting object is Base58 encoded
//        return base58.b58encode(step_4)
    @NonNull
    public static String getAddress(byte[] publicKey) {
//        byte[] publicKeyPrefixed = new byte[publicKey.length+2];
//        //add 2 bytes at beginning
//        publicKeyPrefixed[0] = 1;
//        publicKeyPrefixed[1] = 0;
//        //copy publicKey into publicKeyPrefixed from pos 2 to the end
//        System.arraycopy(publicKey,0,publicKeyPrefixed,2,publicKeyPrefixed.length-2);
//        publicKey = publicKeyPrefixed;
        //step_1
        String value = SecureHelper.sha256AsBase64String(publicKey);
        //step_2
        byte[] ripemd160 = SecureHelper.ripemd160(value);
        //step_3
        byte[] ripemd160Prefixed = new byte[ripemd160.length+2];
        //add 2 bytes at beginning
        ripemd160Prefixed[0] = 1;
        ripemd160Prefixed[1] = 0;
        //copy ripemd160 into ripemd160Prefixed from pos 2 to the end
        System.arraycopy(ripemd160,0,ripemd160Prefixed,2,ripemd160Prefixed.length-2);
        //step_4
        byte[] doubleSha256 = SecureHelper.sha256(SecureHelper.sha256(ripemd160Prefixed));
        //step_5
        byte[] final_checksum = new byte[ripemd160Prefixed.length+4];
        //copy bytesPrefixed into final_checksum from pos 0 to the last 4
        System.arraycopy(ripemd160Prefixed,0,final_checksum,0,ripemd160Prefixed.length-4);
        //copy last 4 from step_4_checksum into final_checksum at the end
        if (doubleSha256 != null) {
            System.arraycopy(doubleSha256,doubleSha256.length-4,
                    final_checksum,final_checksum.length-4,4 );
        }
//        return SecureHelper.toBase64String(final_checksum);
        return Base58.encode(final_checksum);
    }

    private MessageBody getMessage(Invitation invitation) throws IOException {
        InvitationBody body = new InvitationBody(invitation);
        return  new MessageBody(body);
//        return SecureHelper.parse(messageBody);
    }

    private String getMessageSignature() throws IOException {
        String time = "gqlzaWduYXR1cmXaAqxCZHFtTlMycWZQbk1JVm1PY05JWjdQVy9Da1FpZDB4SlFjS20wTzZHNzNuZUYzYlF5R1R0c3hRd0tPV0d5NFEzNnJNK3pQblU4Ri95R0dkRFozQW1JQUV6Tlp2RjRyc2RIZE42SDg4MFJjNG01YzdVQkk1MWhqOXJ4TXRNY0pXZTVQTUpyNFo4dlhPQUcrTmdVQVNScmtHeHBUNDhJTVlCVkpHQWxIZ3FsaVNTeTJoSzdjdDhHZzlkQk1yVFpWcXFLaVVMNkJ6MGthcmVQazA0WjZrZ2hwOEwyS1ovYjBFM0RnSS9mTVlLMDFjNDl3cUxRRExuZWF4a0NFREhtQUUvYlBha3B2eWJYVk5ldmtEaWtKOVpnbzdsdFhhR1BjVG1jTXZoYjFDVUtieHZISlVFRUpXbVFXb0tCTWhwRzJqWlFtWGJCRFZUczBkUEYzUXlqZnhLelJFWVFTWGJRZk85eFNmdTlwckQrUWtRSmRqdHY2Z2RURHJsU3BMaFloZnJDNXkwclA2amVyQnpzVjVmQkNuMGlzZzB2VllVUGM2M3k0ZW45d3R5UHhhcTlTZ3cvZDhLUmgyS2ZuaG4yNi9QSWxtdlYrU1hYbUFNV2c5TDNOcTdVVXBUNHJNKy9WWGZTNzAvUXRyQmVMdkYvUUFmSkhhRDJFK2RLeEV5cndXdHQ1SEQyVWxBOTlUb01YVk15UHhHaFErOS9Ba21QWVpxS2RzekU3Ym9OYjhQTG44S2dGNUMzYkkrajh4VVhsN3d3Rm9YZ0NlV09VSmdmUHJZV1hETUg2WERRQ3dYZzQyMVN6Y3Jpd2k1dWtKOVhmYjgrVXl6d3locWJQcG9mWEFjZnhHRWJLNVVUZ0NGQlZEMkFRNzFPbytmbnVuYURpNGR1UjV4bWVjbE5FWT2pdGltZXN0YW1wsjE1MjEyMTQ5MDguNzAwMjkxMg==";
        Message message = new Message(messageHash.getBytes(),time);
//            signable_object = OrderedDict()
//            signable_object['messageHash'] = message_hash
//            signable_object['timestamp'] = node_signed_timestamp
//
//            signable_object = msgpack.packb(signable_object)
//
//            return base64.b64encode(msgpack.packb({
//                    'signature': self.sign(signable_object),
//                    'timestamp': node_signed_timestamp
//        })).decode()
        PmSignature pmSignature = new PmSignature(SecureHelper.sign(TfConstants.KEY_ALIAS,
//                SecureHelper.toBytes(message)
                SecureHelper.parse(message).getBytes()
        ),time);
        return SecureHelper.toBase64String(pmSignature);
    }
}
