package com.peermountain.core.odk.utils;

import com.peermountain.core.utils.LogUtils;

/**
 * Created by Galeen on 1/25/2018.
 * class to prevent adding Timber lib for ODK reused code
 */

public class Timber {
    private static String TAG = "PmLog";

    public static void w(String msg) {
        LogUtils.w(TAG, msg);
    }

    public static void d(String msg) {
        LogUtils.d(TAG, msg);
    }

    public static void e(String msg) {
        LogUtils.e(TAG, msg);
    }

    public static void i(String msg) {
        LogUtils.i(TAG, msg);
    }

    public static void i(String msg, Object... args) {
        LogUtils.i(TAG, String.format(msg, args));
    }
    public static void i(Error e) {
        LogUtils.i(TAG, e.getMessage());
    }

    public static void e(String msg, Object... args) {
        LogUtils.e(TAG, String.format(msg, args));
    }
    public static void e(Exception e,String msg, Object... args) {
        LogUtils.e(TAG, String.format(msg, args)+"\n"+e);
    }

    public static void e(Exception e, String msg) {
        LogUtils.e(TAG, msg+"\n"+e);
    }
    public static void e(Exception e) {
        LogUtils.e(TAG, e.getMessage());
    }

    public static void d(String msg, Object... args) {
        LogUtils.d(TAG, String.format(msg, args));
    }

    public static void w(String msg, Object... args) {
        LogUtils.w(TAG, String.format(msg, args));
    }
    public static void w(Exception e,String msg, Object... args) {
        LogUtils.w(TAG, String.format(msg, args)+"\n"+e);
    }

    public static void w(Exception e, String msg) {
        LogUtils.w(TAG, msg+"\n"+e);
    }
    public static void w(Exception e) {
        LogUtils.w(TAG, e.getMessage());
    }
}
