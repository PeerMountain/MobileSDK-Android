package com.peermountain.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.peermountain.R;
import com.peermountain.core.network.NetworkManager;
import com.peermountain.core.network.NetworkRequestHelper;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.Invitation;
import com.peermountain.core.network.teleferique.model.SendObject;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        PeerMountainSDK.logout();//to test login again

        showSplash();
//                finish();
//        authorize();
        NetworkRequestHelper.init();
        NetworkManager.getPublicAddress(null,"https://teleferic-dev.dxmarkets.com/teleferic/",
                new SendObject().setQuery("query {  teleferic {    persona {     address    }  } }"));
        Invitation invitation = new Invitation()
                .setBodyType(TfConstants.BODY_TYPE_INVITATION)
                .setBootstrapNode("https://teleferic-dev.dxmarkets.com/teleferic/")
                .setBootstrapAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
                .setOfferingAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
                .setInviteName("Invite 1")
                .setServiceOfferingID("1")
                .setInviteKey("72x35FDOXuTkxivh7qYlqPU91jVgy607")                ;


        NetworkManager.getPublicAddress(null,"https://teleferic-dev.dxmarkets.com/teleferic/",
                invitation.build());
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    CountDownTimer timer;

    private void showSplash() {
        timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
//                PeerMountainSDK.goHome(StartActivity.this);
                timer = null;
                finish();
            }
        };
        timer.start();
    }
}
