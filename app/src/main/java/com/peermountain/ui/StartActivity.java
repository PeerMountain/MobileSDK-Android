package com.peermountain.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.peermountain.BuildConfig;
import com.peermountain.R;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.odk.XFormActivity;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.PeerMountainSDK;


public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 123;
    private static final int REQUEST_REGISTER = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
// TODO: 12/13/2017 this must be called in Application.onCreate
        //is important to pass Application context to prevent memory leaks
        //for now it holds only 2 values, in future will keep all customizable data
        PeerMountainConfig config = PeerMountainManager.getLastPeerMountainConfig(this);
        if (config == null) {//create
            config = new PeerMountainConfig()
                    .setApplicationContext(getApplicationContext())
                    .setDebug(BuildConfig.DEBUG)
                    .setFontSize(16)
                    .setUserValidTime(1000*60*5)//5min, after that the user will be asked again to authorize
                    .setIdCheckLicense("licence-2017-09-12");//axt file from assets
        } else {//just update
            config.setApplicationContext(getApplicationContext())
                    .setUserValidTime(1000*60*5)
                    .setDebug(BuildConfig.DEBUG);
        }
        PeerMountainSDK.init(config);//ui ready
//        PeerMountainManager.init(config); // must implement ui in the app

//        PeerMountainSDK.logout();//to test login again

//        showSplash();

        XFormActivity.show(this,"https://www.dropbox.com/s/9kj12067gqhst42/Sample%20Form.xml?dl=1");
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
                PeerMountainSDK.goHome(StartActivity.this);
                timer = null;
                finish();
            }
        };
        timer.start();
    }


}
