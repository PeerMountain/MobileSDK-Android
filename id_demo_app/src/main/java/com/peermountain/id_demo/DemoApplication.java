package com.peermountain.id_demo;

import android.app.Application;

import com.peermountain.common.CommonLibConfig;

/**
 * Created by Galeen on 8/8/2018.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CommonLibConfig.init(this, BuildConfig.DEBUG);
    }
}
