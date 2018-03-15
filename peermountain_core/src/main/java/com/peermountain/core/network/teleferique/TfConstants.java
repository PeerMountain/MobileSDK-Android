package com.peermountain.core.network.teleferique;

/**
 * Created by Galeen on 3/14/2018.
 */

public class TfConstants {
    public static final int MESSAGE_TYPE_SYSTEM = 0;
    public static final String MESSAGE_TYPE_REGISTRATION = "REGISTRATION";
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


}
