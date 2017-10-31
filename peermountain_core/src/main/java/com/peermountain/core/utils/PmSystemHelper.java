package com.peermountain.core.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Galeen on 10/9/17.
 */

public class PmSystemHelper {
    public static void hideKeyboard(Activity activity, View v) {
        if(activity==null || v==null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static void showKeyboard(Activity activity, View v) {
        if(activity==null || v==null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInputFromInputMethod(et.getWindowToken(), 0);
        imm.toggleSoftInputFromWindow(v.getWindowToken(),InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showSoftInputKeyboard(Activity activity, View v) {
        if(activity==null || v==null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

}
