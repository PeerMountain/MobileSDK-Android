package com.peermountain.core.network.teleferique.model.body.registration;

import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.body.base.BaseBody;
import com.peermountain.core.network.teleferique.model.body.MessageBodyObject;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class RegistrationBody extends BaseBody implements MessageBodyObject {
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
    public RegistrationBody(String time,String inviteMsgID, String keyProof, String inviteName, String publicKey, String publicNickname) {
        super(time);
        this.inviteMsgID = inviteMsgID;
        this.keyProof = keyProof;
        this.inviteName = inviteName;
        this.publicKey = publicKey;
        this.publicNickname = publicNickname;
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
}