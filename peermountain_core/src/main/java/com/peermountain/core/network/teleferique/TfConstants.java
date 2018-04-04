package com.peermountain.core.network.teleferique;

/**
 * Created by Galeen on 3/14/2018.
 */

public class TfConstants {
    public static final String KEY_ALIAS = "test2";
    public static final String KEY_ADDRESS = "2nHZ7xWEwuZMwdLGzji5vLJxd17ahnfJei3";
    public static final String KEY_PUBLIC = "-----BEGIN PUBLIC KEY-----\n" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAvibs6QJ23DtU01mLVo6F\n" +
            "B9eyj12FpPHvgFvQ39zdRFnZ3jxqvFxENWBFrBV4x11enh4U3djBg2QhYuiEVYlf\n" +
            "Tto9NEGQtRz5g5kaM3yiZMVXIkyVmdXvU0cSsQqQP00lt2tm4zdClxVvwt3oN2Kn\n" +
            "xLH6aO/ENw64fp4rqSq8zJcjYNBGdVjFSNkWj7wxeOrGgUockIDxGmlfcbF/YRxL\n" +
            "PrJbEerx6ClkRwPVlgof8Lvs2uaEcPuO0POC3R3+sMVE4d627tAl6KR2eW/98Rdn\n" +
            "I2bQYcUzK9L9/X3lU28L5sUJQBqtsoEcEbOYxylAEkBm9jPn71fV2245oKbs6YBm\n" +
            "hRNx+lnw9DugLrB4T2Yzu+3JNR5FNXD+SkW8Ay1vcPmAeMEAsvHoXNUxVJzd5hwD\n" +
            "FIMrDuUuiP7jF+PNh4SGaUgUIgbk36rrgMP8z0xrnbENh9/uHhBSahRHb7a3DAwY\n" +
            "dwMdk5AZm3lGWL9+I+YPFEHpSY6zy3y9ZNxcpq2LDvERMMW6NqHue8tPII6utT6N\n" +
            "1ExGn2O6pi7RQEs7ZvK4Mpeys5ZSsfcnFbRMrNVbYBq+btUYw1/FP/P/YGJ7CQHl\n" +
            "ID6ytYdrODPBftAv4e1avmqCit+7MZyJME2zxG71kBJa59qcvQXf3AoZxfj0tnHG\n" +
            "onmwCjRva9XmguDORNL460sCAwEAAQ==\n" +
            "-----END PUBLIC KEY-----";
    public static final int MESSAGE_TYPE_SYSTEM = 0;
    public static final String MESSAGE_TYPE_REGISTRATION = "REGISTRATION";//1;
    public static final int MESSAGE_TYPE_ASSERTION = 2;
    public static final int MESSAGE_TYPE_ATTESTATION = 3;
    public static final int MESSAGE_TYPE_SERVICE = 4;
    public static final int MESSAGE_TYPE_DELEGATIONS = 5;

    // System
    public static final int BODY_TYPE_CHECKPOINT = 0;

    // Registration
    public static final int BODY_TYPE_INVITATION = 0;
    public static final int BODY_TYPE_REGISTRATION = 1;
    public static final int BODY_TYPE_REGISTRATION_DELEGATION = 2;
    public static final int BODY_TYPE_BACKUP = 3;
    public static final int BODY_TYPE_RECOVER = 4;

    // Assertion - Not Defined

    // Attestation
    public static final int BODY_TYPE_DIRECT_ATTESTATION = 0;
    public static final int BODY_TYPE_VERIFICATION_REQUEST = 1;

    // Service
    public static final int BODY_TYPE_SERVICE_OFFERING = 0;
    public static final int BODY_TYPE_SERVICE_REQUEST = 1;
    public static final int BODY_TYPE_AMENDMENT_REQUEST = 2;

    // Delegation
    public static final int BODY_TYPE_DELEGATION = 0;
    public static final int BODY_TYPE_VALIDATION = 1;


    public static final String TELEFERIC_QUERY = "mutation (\n" +
            "                $sender: Address!\n" +
            "                $messageType: MessageType!\n" +
            "                $messageHash: SHA256!\n" +
            "                $bodyHash: SHA256!\n" +
            "                $messageSig: Sign!\n" +
            "                $message: AESEncryptedBlob!\n" +
            "                $dossierHash: HMACSHA256!\n" +
            "                ){\n" +
            "                sendMessage(\n" +
            "                    envelope: {\n" +
            "                    sender: $sender\n" +
            "                    messageType: $messageType\n" +
            "                    messageHash: $messageHash\n" +
            "                    bodyHash: $bodyHash\n" +
            "                    messageSign: $messageSig\n" +
            "                    message: $message\n" +
            "                    dossierHash: $dossierHash\n" +
            "                    }\n" +
            "                ) {\n" +
            "                    messageHash\n" +
            "                }\n" +
            "            }";

    public static final String TELEFERIC_QUERY_ACL = "mutation (\n" +
            "                $sender: Address!\n" +
            "                $messageType: MessageType!\n" +
            "                $messageHash: SHA256!\n" +
            "                $bodyHash: SHA256!\n" +
            "                $messageSig: Sign!\n" +
            "                $message: AESEncryptedBlob!\n" +
            "                $dossierHash: HMACSHA256!\n" +
            "                $ACL: [ACLRule]\n" +
            "                $objects: [ObjectInput]\n" +
            "                ){\n" +
            "                sendMessage(\n" +
            "                    envelope: {\n" +
            "                    sender: $sender\n" +
            "                    messageType: $messageType\n" +
            "                    messageHash: $messageHash\n" +
            "                    bodyHash: $bodyHash\n" +
            "                    messageSign: $messageSig\n" +
            "                    message: $message\n" +
            "                    dossierHash: $dossierHash\n" +
            "                    $objects: $objects\n" +
            "                    ACL: $ACL\n" +
            "                    }\n" +
            "                ) {\n" +
            "                    messageHash\n" +
            "                }\n" +
            "            }";
}
