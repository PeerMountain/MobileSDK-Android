package com.peermountain.core.network.teleferique.model;

/**
 * Created by Galeen on 3/14/2018.
 */

public class Invitation extends PublicEnvelope{
    private int bodyType;//from TfConstants
    private String bootstrapNode; // URL or other trigger to open/install app
    private String bootstrapAddr; // PM Address
    private String offeringAddr; // PM Address
    private String serviceOfferingID; // sha256
    private String inviteName; // text // Encrypted AES-256 using the inviteKey
    private String inviteMsgID; // sha256 // Not present on Himalaya - serviceAnnouncementMessage
    private String inviteKey; // key // Not present on Himalaya

    public int getBodyType() {
        return bodyType;
    }

    public Invitation setBodyType(int bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public String getBootstrapNode() {
        return bootstrapNode;
    }

    public Invitation setBootstrapNode(String bootstrapNode) {
        this.bootstrapNode = bootstrapNode;
        return this;
    }

    public String getBootstrapAddr() {
        return bootstrapAddr;
    }

    public Invitation setBootstrapAddr(String bootstrapAddr) {
        this.bootstrapAddr = bootstrapAddr;
        return this;
    }

    public String getOfferingAddr() {
        return offeringAddr;
    }

    public Invitation setOfferingAddr(String offeringAddr) {
        this.offeringAddr = offeringAddr;
        return this;
    }

    public String getServiceOfferingID() {
        return serviceOfferingID;
    }

    public Invitation setServiceOfferingID(String serviceOfferingID) {
        this.serviceOfferingID = serviceOfferingID;
        return this;
    }

    public String getInviteName() {
        return inviteName;
    }

    public Invitation setInviteName(String inviteName) {
        this.inviteName = inviteName;
        return this;
    }

    public String getInviteMsgID() {
        return inviteMsgID;
    }

    public Invitation setInviteMsgID(String inviteMsgID) {
        this.inviteMsgID = inviteMsgID;
        return this;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public Invitation setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
        return this;
    }
}
