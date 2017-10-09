package com.peermountain.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.peermountain.BuildConfig;
import com.peermountain.MainActivity;
import com.peermountain.R;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;


public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //is important to pass Application context to prevent memory leaks
        //for now it holds only 2 values, in future will keep all customizable data
        PeerMountainConfig config = PeerMountainManager.getLastPeerMountainConfig(this);
        if(config==null){//create
            config = new PeerMountainConfig()
                    .setApplicationContext(getApplicationContext())
                    .setDebug(BuildConfig.DEBUG)
                    .setIdCheckLicense("licence-2017-09-12");//axt file from assets
        }else{//just update
            config.setApplicationContext(getApplicationContext())
                    .setDebug(BuildConfig.DEBUG);
        }
        PeerMountainSDK.init(config);//ui ready
//        PeerMountainManager.init(config); // must implement ui in the app

//        PeerMountainSDK.logout();//to test login

        showSplash();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK && data != null) {
                    goToMain((PublicUser) data.getParcelableExtra(PeerMountainSdkConstants.EXTRA_PUBLIC_USER));
                } else {
                    Toast.makeText(this, "Login refused!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void goToMain(PublicUser publicUser) {
        MainActivity.show(this, publicUser);
        finish();
    }

    private void goToRegister(PublicUser publicUser) {
        MainActivity.show(this, publicUser);
        finish();
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
                PeerMountainSDK.registerFlow(StartActivity.this, REQUEST_LOGIN);
//                if(PeerMountainManager.getPublicUser()!=null){
//                    goToMain(PeerMountainManager.getPublicUser());
//                }else {
//                    PeerMountainSDK.authorize(StartActivity.this, REQUEST_LOGIN);
//                }
                timer = null;
            }
        };
        timer.start();
    }


}
