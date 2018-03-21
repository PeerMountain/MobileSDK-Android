package com.peermountain.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.peermountain.core.persistence.PeerMountainManager;

/**
 * Created by Galeen on 10/9/17.
 */

public class PmSystemHelper {

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus() == null ? activity.findViewById(android.R.id.content) : activity.getCurrentFocus();
        hideKeyboard(activity, view);
    }

    public static void hideKeyboard(Activity activity, View v) {
        if (activity == null || v == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity, View v) {
        if (activity == null || v == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInputFromInputMethod(et.getWindowToken(), 0);
        imm.toggleSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showSoftInputKeyboard(Activity activity, View v) {
        if (activity == null || v == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Point getScreenSize(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return null;
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // include navigation bar
            display.getRealSize(outPoint);
        } else {
            // exclude navigation bar
            display.getSize(outPoint);
        }
        if (outPoint.y > outPoint.x) {
            outPoint.set(outPoint.y, outPoint.x);
        }
        return outPoint;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private static Boolean isRunningTest;

    public static synchronized boolean isRunningTest() {
        if (PeerMountainManager.getApplicationContext() == null) return true;
        if (null == isRunningTest) {
            try {
                Class.forName(PeerMountainManager.getApplicationContext().getPackageName() + ".ExampleUnitTest");
                isRunningTest = true;
            } catch (ClassNotFoundException e) {
                isRunningTest = false;
            }
        }
        return isRunningTest;
    }

}
