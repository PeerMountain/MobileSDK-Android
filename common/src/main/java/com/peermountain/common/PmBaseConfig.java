package com.peermountain.common;

import android.content.Context;

/**
 * Created by Galeen on 8/8/2018.
 */
public class PmBaseConfig {
    private static Context applicationContext = null;
    private static boolean debug = false;

    public static void init(Context applicationContext, boolean debug) {
        PmBaseConfig.applicationContext = applicationContext;
        PmBaseConfig.debug = debug;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static boolean isDebug() {
        return debug;
    }
}
