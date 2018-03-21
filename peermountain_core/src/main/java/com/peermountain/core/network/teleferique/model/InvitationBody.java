package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.network.teleferique.TfConstants;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class InvitationBody implements MessageBodyObject{
    private String bootstrapNode; // URL or other trigger to open/install app
    private String bootstrapAddr; // PM Address
    private String inviteName; // text // Encrypted AES-256 using the inviteKey
    private String offeringAddr; // PM Address
    private String serviceOfferingID; // sha256
    private String serviceAnnouncementMessage;


//    private String inviteMsgID; // sha256 // Not present on Himalaya - serviceAnnouncementMessage
//    private String inviteKey; // key // Not present on Himalaya

    public InvitationBody() {
    }

    public InvitationBody(Invitation in) {
        this.bootstrapNode = in.getBootstrapNode();
        this.bootstrapAddr = in.getBootstrapAddr();
        this.inviteName = in.getInviteName();
        this.offeringAddr = in.getOfferingAddr();
        this.serviceOfferingID = in.getServiceOfferingID();
        this.serviceAnnouncementMessage = in.getServiceAnnouncementMessage();
    }

    public InvitationBody(int bodyType, String bootstrapNode, String bootstrapAddr, String inviteName, String offeringAddr, String serviceOfferingID, String serviceAnnouncementMessage) {
        this.bootstrapNode = bootstrapNode;
        this.bootstrapAddr = bootstrapAddr;
        this.inviteName = inviteName;
        this.offeringAddr = offeringAddr;
        this.serviceOfferingID = serviceOfferingID;
        this.serviceAnnouncementMessage = serviceAnnouncementMessage;
    }

    public String getBootstrapNode() {
        return bootstrapNode;
    }

    public void setBootstrapNode(String bootstrapNode) {
        this.bootstrapNode = bootstrapNode;
    }

    public String getBootstrapAddr() {
        return bootstrapAddr;
    }

    public void setBootstrapAddr(String bootstrapAddr) {
        this.bootstrapAddr = bootstrapAddr;
    }

    public String getInviteName() {
        return inviteName;
    }

    public void setInviteName(String inviteName) {
        this.inviteName = inviteName;
    }

    public String getOfferingAddr() {
        return offeringAddr;
    }

    public void setOfferingAddr(String offeringAddr) {
        this.offeringAddr = offeringAddr;
    }

    public String getServiceOfferingID() {
        return serviceOfferingID;
    }

    public void setServiceOfferingID(String serviceOfferingID) {
        this.serviceOfferingID = serviceOfferingID;
    }

    public String getServiceAnnouncementMessage() {
        return serviceAnnouncementMessage;
    }

    public void setServiceAnnouncementMessage(String serviceAnnouncementMessage) {
        this.serviceAnnouncementMessage = serviceAnnouncementMessage;
    }

    @Override
    public int takeBodyType() {
        return TfConstants.BODY_TYPE_INVITATION;
    }

//    public String getInviteMsgID() {
//        return inviteMsgID;
//    }
//
//    public void setInviteMsgID(String inviteMsgID) {
//        this.inviteMsgID = inviteMsgID;
//    }
//
//    public String getInviteKey() {
//        return inviteKey;
//    }
//
//    public void setInviteKey(String inviteKey) {
//        this.inviteKey = inviteKey;
//    }
}