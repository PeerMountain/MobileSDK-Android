package com.peermountain.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;

import java.io.File;


/**
 * Created by Galeen on 18.1.2016 г..
 * It has public static methods which correspond to each server service.
 * They accept a MainCallback(to talk to the caller) and data to send.
 * The NetworkManager creates/parses the data and constructs an Action object which holds all
 * the information for the request, than according to the request is send to the ServerOperation(AsyncTask)
 * as parallel or synchronized request. Before to send it to the ServerOperation it checks for
 * internet connection and if hasn't will notify the MainCallback.onNoNetwork for that and stop the process.
 */
public class NetworkManager {

    private static AsyncTask<Action, Void, NetworkResponse> doMainActionParallel(MainCallback mCallback, Action action, String log) {
        return doMainAction(mCallback, action, log, true);

    }

    private static AsyncTask<Action, Void, NetworkResponse> doMainActionSynchronized(MainCallback mCallback, Action action, String log) {
        return doMainAction(mCallback, action, log, false);

    }

    private static AsyncTask<Action, Void, NetworkResponse> doMainAction(MainCallback mCallback, Action action, String log,
                                                                         boolean concurrent) {
        LogUtils.d("NetworkManager ", log + " ...");
//        if (mCallback != null) {
            if (isNetworkAvailable()) {
                if (concurrent) {
                    return new ServerOperation(mCallback, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, action);
                } else {
                    return new ServerOperation(mCallback, null).execute(action);
                }
            } else {
                mCallback.onNoNetwork();
                return null;
            }
//        } else {
//            return null;
//        }
    }

    private static NetworkResponse doMainActionOnSameThread(Action action, String log) {
        LogUtils.d("NetworkManager ", log + " ...");
        return ServerOperation.doServerCall(action, null);
    }

    public static AsyncTask<Action, Void, NetworkResponse> downloadXForm(MainCallback mCallback,String url, File intoFile) {
        AsyncTask<Action, Void, NetworkResponse> serverOperation =  doMainActionSynchronized(mCallback, Actions.getForm(intoFile,url), "downloadXForm... ");
        mCallback.setTask(serverOperation);
        return serverOperation;
    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) PeerMountainManager.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

