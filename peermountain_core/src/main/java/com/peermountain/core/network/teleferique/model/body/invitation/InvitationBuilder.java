package com.peermountain.core.network.teleferique.model.body.invitation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.PublicEnvelope;
import com.peermountain.core.network.teleferique.model.SendObject;
import com.peermountain.core.network.teleferique.model.body.base.BaseBuilder;

import java.io.Serializable;

/**
 * Created by Galeen on 3/14/2018.
 */

public class InvitationBuilder extends BaseBuilder implements Serializable {
    private String bootstrapNode; // URL or other trigger to open/install app
    private String bootstrapAddr; // PM Address
    private String offeringAddr; // PM Address
    private String serviceOfferingID; // sha256
    private String inviteName; // text // Encrypted AES-256 using the inviteKey
    private String inviteMsgID; // sha256 // Not present on Himalaya - serviceAnnouncementMessage
    private String inviteKey; // key // Not present on Himalaya
    private String serviceAnnouncementMessage;

    public InvitationBuilder() {
        super(TfConstants.BODY_TYPE_INVITATION,null);
    }

    public InvitationBuilder(int bodyType, String time) {
        super(bodyType, time);
    }

    public String getBootstrapNode() {
        return bootstrapNode;
    }

    public InvitationBuilder setBootstrapNode(String bootstrapNode) {
        this.bootstrapNode = bootstrapNode;
        return this;
    }

    public String getBootstrapAddr() {
        return bootstrapAddr;
    }

    public InvitationBuilder setBootstrapAddr(String bootstrapAddr) {
        this.bootstrapAddr = bootstrapAddr;
        return this;
    }

    public String getOfferingAddr() {
        return offeringAddr;
    }

    public InvitationBuilder setOfferingAddr(String offeringAddr) {
        this.offeringAddr = offeringAddr;
        return this;
    }

    public String getServiceOfferingID() {
        return serviceOfferingID;
    }

    public InvitationBuilder setServiceOfferingID(String serviceOfferingID) {
        this.serviceOfferingID = serviceOfferingID;
        return this;
    }

    public String getInviteName() {
        return inviteName;
    }

    public InvitationBuilder setInviteName(String inviteName) {
        this.inviteName = inviteName;
        return this;
    }

    public String getInviteMsgID() {
        return inviteMsgID;
    }

    public InvitationBuilder setInviteMsgID(String inviteMsgID) {
        this.inviteMsgID = inviteMsgID;
        return this;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public InvitationBuilder setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
        return this;
    }

    public String getServiceAnnouncementMessage() {
        return serviceAnnouncementMessage;
    }

    public InvitationBuilder setServiceAnnouncementMessage(String serviceAnnouncementMessage) {
        this.serviceAnnouncementMessage = serviceAnnouncementMessage;
        return this;
    }

    @Override
    public SendObject build() {
        PublicEnvelope envelope = new PublicEnvelope(this);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String variables = gson.toJson(envelope, PublicEnvelope.class);
//        .replace("\\\\u003d","=");

        return new SendObject().setQuery(
                TfConstants.TELEFERIC_QUERY)
                .setVariables(variables);
    }
}
