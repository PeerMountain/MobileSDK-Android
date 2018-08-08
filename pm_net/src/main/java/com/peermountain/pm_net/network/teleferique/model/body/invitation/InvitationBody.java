package com.peermountain.pm_net.network.teleferique.model.body.invitation;

import com.peermountain.pm_net.network.teleferique.TfConstants;
import com.peermountain.pm_net.network.teleferique.model.body.base.BaseBody;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class InvitationBody extends BaseBody {
    private String bootstrapNode; // URL or other trigger to open/install app
    private String bootstrapAddr; // PM Address
    private String inviteName; // text // Encrypted AES-256 using the inviteKey
    private String offeringAddr; // PM Address
    private String serviceOfferingID; // sha256
    private String serviceAnnouncementMessage;


//    private String inviteMsgID; // sha256 // Not present on Himalaya - serviceAnnouncementMessage
//    private String inviteKey; // key // Not present on Himalaya

    public InvitationBody() {
        super(null);
    }

    public InvitationBody(InvitationBuilder in) {
        super(in.getTime());
        this.bootstrapNode = in.getBootstrapNode();
        this.bootstrapAddr = in.getBootstrapAddr();
        this.inviteName = in.getInviteName();
        this.offeringAddr = in.getOfferingAddr();
        this.serviceOfferingID = in.getServiceOfferingID();
        this.serviceAnnouncementMessage = in.getServiceAnnouncementMessage();
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

    @Override
    public String takeMessageType() {
        return TfConstants.MESSAGE_TYPE_REGISTRATION;
    }
}