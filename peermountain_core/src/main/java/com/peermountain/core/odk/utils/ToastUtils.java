package com.peermountain.core.odk.utils;

import android.widget.Toast;

import com.peermountain.core.persistence.PeerMountainManager;


public class ToastUtils {


    public static void showShortToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int messageResource) {
        showToast(messageResource, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    public static void showLongToast(int messageResource) {
        showToast(messageResource, Toast.LENGTH_LONG);
    }

    private static void showToast(String message, int duration) {
        Toast.makeText(PeerMountainManager.getApplicationContext(), message, duration).show();
    }

    private static void showToast(int messageResource, int duration) {
        Toast.makeText(PeerMountainManager.getApplicationContext(), PeerMountainManager.getApplicationContext().getString(messageResource), duration).show();
    }
}
