package com.peermountain.id_demo;

import android.app.Application;

import com.peermountain.common.PmBaseConfig;

/**
 * Created by Galeen on 8/8/2018.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new PmBaseConfig.Builder()
                .setApplicationContext(this)
                .setDebug(BuildConfig.DEBUG)
                .setApiScanKey("bStfjjadHizdxqabdcStOg==")
                .init();
    }
}
