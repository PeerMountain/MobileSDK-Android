package com.peermountain;

import android.support.multidex.MultiDexApplication;

import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.PeerMountainSDK;

/**
 * Created by Galeen on 2/26/18.
 * Application class
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //is important to pass Application context to prevent memory leaks
        PeerMountainConfig config = PeerMountainManager.getLastPeerMountainConfig(this);
        if (config == null) {//create
            config = new PeerMountainConfig()
                    .setApplicationContext(getApplicationContext())
                    .setDebug(BuildConfig.DEBUG)
                    .setApiScanKey("bStfjjadHizdxqabdcStOg==")
                    .setFontSize(16)
                    .setUserValidTime(1000 * 60 * 5)//5min, after that the user will be asked again to authorize
                    .setIdCheckLicense("licence-2017-09-12");//axt file from assets
        } else {//just update
            config.setApplicationContext(getApplicationContext())
                    .setUserValidTime(1000 * 60 * 5)
                    .setDebug(BuildConfig.DEBUG)
                    .setApiScanKey("bStfjjadHizdxqabdcStOg==");
        }
        PeerMountainSDK.init(config);//ui ready
//        PeerMountainManager.init(config); // must implement ui in the app
    }
}
