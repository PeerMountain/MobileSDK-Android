package com.peermountain.pm_net.network.teleferique.model.body.registration;

import com.peermountain.pm_net.network.teleferique.TfConstants;
import com.peermountain.pm_net.network.teleferique.model.body.base.BaseBody;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class RegistrationBody extends BaseBody{
    private String inviteMsgID; // sha256
    private String keyProof; // Teleferique pubKey(invKey)
    private String inviteName; // Teleferique pubKey(inviteName)
    private String publicKey; // RSA pubkey
    private String publicNickname; // text

    public RegistrationBody() {
        super(null);
    }

    /*:param inviteMsgID: ID of the Invite message the client received.
      :param inviteKey: string: Key of the invite. Shared secret between inviter and invitee.
      :param inviteName: string: Name of the invite. Shared secret between inviter and invitee.
      :param nickname: Undocumented parameter.*/
    public RegistrationBody(RegistrationBuilder builder) {
        super(builder.getTime());
        this.inviteMsgID = builder.getInviteMsgID();
        this.keyProof = builder.getKeyProof();
        this.inviteName = builder.getInviteName();
        this.publicKey = builder.getPublicKey();
        this.publicNickname = builder.getPublicNickname();
    }

    public String getInviteMsgID() {
        return inviteMsgID;
    }

    public void setInviteMsgID(String inviteMsgID) {
        this.inviteMsgID = inviteMsgID;
    }

    public String getKeyProof() {
        return keyProof;
    }

    public void setKeyProof(String keyProof) {
        this.keyProof = keyProof;
    }

    public String getInviteName() {
        return inviteName;
    }

    public void setInviteName(String inviteName) {
        this.inviteName = inviteName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicNickname() {
        return publicNickname;
    }

    public void setPublicNickname(String publicNickname) {
        this.publicNickname = publicNickname;
    }

    @Override
    public int takeBodyType() {
        return TfConstants.BODY_TYPE_REGISTRATION;
    }

    @Override
    public String takeMessageType() {
        return TfConstants.MESSAGE_TYPE_REGISTRATION;
    }
}