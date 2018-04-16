package com.peermountain.core.network.teleferique.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.body.MessageContent;
import com.peermountain.core.network.teleferique.model.body.invitation.InvitationBuilder;
import com.peermountain.core.network.teleferique.model.body.registration.RegistrationBuilder;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.secure.Base58;
import com.peermountain.core.secure.SecureHelper;
import com.peermountain.core.utils.LogUtils;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

/**
 * Created by Galeen on 3/14/2018.
 */

public class PublicEnvelope {
    private transient final String pass = "Peer Mountain";
    public String messageHash; // sha256
    public String messageType; // typecode , from TfConstants
    public String dossierHash; // hmac-sha256
    public String bodyHash; //sha256
    public String messageSig; // RSA signature
    public String message; // (enc)BLOB - AESEncryptedBlob!
    public String sender; // PM Address

    public PublicEnvelope() {
    }

    public PublicEnvelope(InvitationBuilder invitationBuilder) {
        //AES encrypt body with passphrase='Peer Mountain'
        invitationBuilder.setInviteName(SecureHelper.encodeAES(pass, invitationBuilder.getInviteName()));
        LogUtils.d("name encoded", invitationBuilder.getInviteName());
        LogUtils.d("name decoded", SecureHelper.decodeAES(pass, invitationBuilder.getInviteName()));

        MessageContent messageContent = invitationBuilder.getMessageContent();
        compose(messageContent);
    }

    public PublicEnvelope(RegistrationBuilder registrationBuilder) {

        MessageContent messageContent = registrationBuilder.getMessageContent();
        compose(messageContent);
    }

    private void compose(MessageContent messageContent) {
        LogUtils.d("message content", messageContent.asJson());

        //AES encrypt body with passphrase='Peer Mountain'
        String encryptedBody = SecureHelper.encodeAES(pass, SecureHelper.parse(messageContent));
        message = encryptedBody;
        LogUtils.d("message encoded", encryptedBody);
//            MessageContent messageAsMap = null;
//            try {
//                messageAsMap = (RegistrationMessage) SecureHelper.read(
//                        SecureHelper.decodeAES(pass, Base64.decode(encryptedBody, Base64.NO_WRAP)),
//                        RegistrationMessage.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (messageAsMap != null) {
//                LogUtils.d("message decoded", messageAsMap.getBodyType() + "");
////                LogUtils.d("message_body decoded", ((InvitationBody)SecureHelper.read(messageAsMap.getMessageBody(),InvitationBody.class)).getBootstrapAddr());
//            }
        messageHash = SecureHelper.sha256AsBase64String(encryptedBody);

        bodyHash = messageContent.takeBodyHash();//SecureHelper.sha256AsBase64String(messageBody);

        messageType = messageContent.takeMessageType();

        dossierHash = SecureHelper.hash_hmac_simple(messageContent.takeSalt()
                , messageContent.takeMessageBodyPacked());
//            dossierHash = SecureHelper.hash_hmacAsB64(pass,messageContent.messageBodyPacked,salt);

        messageSig = getMessageSignature(messageContent.takeTime(), messageHash);

        verifySignature(messageSig, messageHash);

        KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(PeerMountainManager.getApplicationContext(), TfConstants.KEY_ALIAS);
        sender = getAddress(keyPair.getPublic().getEncoded());//SecureHelper.toPEM(keyPair.getPublic()).getBytes());

//            LogUtils.d("pkey", SecureHelper.toPEM(keyPair.getPublic()));
        LogUtils.d("envelope", new Gson().toJson(this));
    }

//    private void compose(MessageBody messageBody) {
//        try {
//            //AES encrypt body with passphrase='Peer Mountain'
//            String encryptedBody = SecureHelper.encodeAES(pass, SecureHelper.parse(messageBody));
//            message = encryptedBody;
//            LogUtils.d("message encoded", encryptedBody);
//            MessageBody messageAsMap = null;
//            try {
//                messageAsMap = (MessageBody) SecureHelper.read(
//                        SecureHelper.decodeAES(pass, Base64.decode(encryptedBody, Base64.NO_WRAP)),
//                        MessageBody.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (messageAsMap != null) {
//                LogUtils.d("message decoded", messageAsMap.getBodyType() + "");
////                LogUtils.d("message_body decoded", ((InvitationBody)SecureHelper.read(messageAsMap.getMessageBody(),InvitationBody.class)).getBootstrapAddr());
//            }
//
//            messageHash = SecureHelper.sha256AsBase64String(encryptedBody);
////            LogUtils.d("encrypted messageHash", messageHash);
//
//            bodyHash = messageBody.bodyHash;//SecureHelper.sha256AsBase64String(messageBody);
////            LogUtils.d("full bodyHash", bodyHash);
//
//            messageType = TfConstants.MESSAGE_TYPE_REGISTRATION;
////            LogUtils.d("messageType", messageType+"");
//
//            dossierHash = SecureHelper.hash_hmac(pass, messageBody.getMessageBody());
////            LogUtils.d("dossierHash", dossierHash);
//
//            messageSig = getMessageSignature(messageBody);
////            LogUtils.d("messageSig", messageSig);
//
//
//            KeyPair keyPair = SecureHelper.getOrCreateAndroidKeyStoreAsymmetricKey(PeerMountainManager.getApplicationContext(), TfConstants.KEY_ALIAS);
//            sender = "2nHZ7xWEwuZMwdLGzji5vLJxd17ahnfJei3";//getAddress(keyPair.getPublic().getEncoded());//SecureHelper.toPEM(keyPair.getPublic()).getBytes());
//
//            LogUtils.d("pkey", SecureHelper.toPEM(keyPair.getPublic()));
////            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//            LogUtils.d("envelope", new Gson().toJson(this));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
    public static String getAddress(byte[] publicKey) { // TODO: 4/4/2018 keep local
        //step_1
        String value = SecureHelper.sha256AsBase64String(publicKey);
        //step_2
        byte[] ripemd160 = SecureHelper.ripemd160(value);
        //step_3
        byte[] ripemd160Prefixed = new byte[ripemd160.length + 2];
        //add 2 bytes at beginning
        ripemd160Prefixed[0] = 1;
        ripemd160Prefixed[1] = 0;
        //copy ripemd160 into ripemd160Prefixed from pos 2 to the end
        System.arraycopy(ripemd160, 0, ripemd160Prefixed, 2, ripemd160Prefixed.length - 2);
        //step_4
        byte[] doubleSha256 = SecureHelper.sha256(SecureHelper.sha256(ripemd160Prefixed));
        //step_5
        byte[] final_checksum = new byte[ripemd160Prefixed.length + 4];
        //copy bytesPrefixed into final_checksum from pos 0 to the last 4
        System.arraycopy(ripemd160Prefixed, 0, final_checksum, 0, ripemd160Prefixed.length - 4);
        //copy last 4 from step_4_checksum into final_checksum at the end
        if (doubleSha256 != null) {
            System.arraycopy(doubleSha256, doubleSha256.length - 4,
                    final_checksum, final_checksum.length - 4, 4);
        }
        return Base58.encode(final_checksum);
    }

    public static String getMessageSignature(String time, String hash) {
        MessageForSignature messageForSignature = new MessageForSignature(hash, time);
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
                SecureHelper.parse(messageForSignature)
        ), time);
        return SecureHelper.parseToBase64(pmSignature);
    }

    public static String getMessageSignature(String time, String hash, PrivateKey privateKey) {
        //MessageForSignature is just data holder
        MessageForSignature messageForSignature = new MessageForSignature(hash, time);
        //PmSignature is just data holder
        PmSignature pmSignature = null;
        try {
            pmSignature = new PmSignature(
                    SecureHelper.sign(
                            SecureHelper.parse(messageForSignature)//convert to map and pack
                            , privateKey
                    ),
                    time
            );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return SecureHelper.parseToBase64(pmSignature);//convert to map , pack and encodeBase64
    }

    private void verifySignature(String base64Sign, String hash) {
        PmSignature pmSignature = (PmSignature) SecureHelper.read(base64Sign, PmSignature.class);
        if (pmSignature == null) return;
        MessageForSignature messageForSignature = new MessageForSignature(hash, pmSignature.getTimestamp());
        LogUtils.d("verify", SecureHelper.verify(TfConstants.KEY_ALIAS, SecureHelper.parse(messageForSignature), pmSignature.getSignature()) + "");
    }
}
