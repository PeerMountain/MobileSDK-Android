package com.peermountain.core.network;

import android.content.DialogInterface;


/**
 * Created by Galeen on 12/21/2017.
 */

public interface BaseEvents {
    void removeCallback(MainCallback callback);
    void addCallback(MainCallback callback);
    void hideProgressBar();
    void hideProgressDialog();
    void showProgressBar();
    void showProgressDialog(String loadingMsg, DialogInterface.OnCancelListener onCancelListener);
    void showNoNetwork();
    void showError(String msg, NetworkResponse networkResponse);
}
