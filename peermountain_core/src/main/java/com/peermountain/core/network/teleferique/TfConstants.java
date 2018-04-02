package com.peermountain.core.network.teleferique;

/**
 * Created by Galeen on 3/14/2018.
 */

public class TfConstants {
    public static final String KEY_ALIAS = "test2";
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
