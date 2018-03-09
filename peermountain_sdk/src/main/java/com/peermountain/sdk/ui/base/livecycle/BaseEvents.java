package com.peermountain.sdk.ui.base.livecycle;

import android.content.DialogInterface;

import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkResponse;


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
