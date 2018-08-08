package com.peermountain.common;

import android.content.Context;

/**
 * Created by Galeen on 8/8/2018.
 */
public class CommonLibConfig {
    private static Context applicationContext = null;
    private static boolean debug = false;

    public static void init(Context applicationContext, boolean debug) {
        CommonLibConfig.applicationContext = applicationContext;
        CommonLibConfig.debug = debug;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static boolean isDebug() {
        return debug;
    }
}
