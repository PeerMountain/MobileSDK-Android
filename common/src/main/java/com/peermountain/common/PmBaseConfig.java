package com.peermountain.common;

import android.content.Context;

/**
 * Created by Galeen on 8/8/2018.
 */
public class PmBaseConfig {
    private static Builder builder;

    public static void init(Context applicationContext, boolean debug) {
    }

    public static Context getApplicationContext() {
        return builder.applicationContext;
    }

    public static boolean isDebug() {
        return builder.debug;
    }

    public static String getApiScanKey() {
        return builder.apiScanKey;
    }

    /**
     *
     */
    public static  class Builder{
        private Context applicationContext = null;
        private boolean debug = false;
        private String apiScanKey;

        public void init(){
            PmBaseConfig.builder = this;
        }

        public Builder setApplicationContext(Context applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setApiScanKey(String apiScanKey) {
            this.apiScanKey = apiScanKey;
            return this;
        }
    }
}
