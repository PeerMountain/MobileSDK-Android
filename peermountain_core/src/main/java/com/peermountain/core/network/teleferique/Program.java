package com.peermountain.core.network.teleferique;


import com.peermountain.core.network.teleferique.model.PublicEnvelope;
import com.peermountain.core.network.teleferique.model.Invitation;
import com.peermountain.core.network.teleferique.model.InvitationResponse;

public class Program {


    //    serviceAnnouncementMessage='L+ViP+UFnhc6ObWfhugqNZfE+SZkqoS46I4Qbw+NbOY=',
    public static void main(String[] args) {
        Invitation invitation = new Invitation()
        .setBootstrapNode("https://teleferic-dev.dxmarkets.com/teleferic")
        .setBootstrapAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
        .setOfferingAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
        .setServiceOfferingID("1")
        .setInviteName("Invite 1")
//        invitation.inviteMsgID = ;
        .setInviteKey("72x35FDOXuTkxivh7qYlqPU91jVgy607");

        final InvitationResponse invitationResponse = new InvitationResponse();
        invitationResponse.inviteMsgID = ""; // result['data']['sendMessage']['messageHash']
        invitationResponse.keyProof = ""; // inviteKey encrypted by the deployment machine persona publicKey
        invitationResponse.ïnviteName = invitation.getInviteName();
        invitationResponse.publicKey = invitation.getInviteKey();
        invitationResponse.publicNickname = "Futurist";


        final PublicEnvelope publicEnvelope = new PublicEnvelope();
    }

}
