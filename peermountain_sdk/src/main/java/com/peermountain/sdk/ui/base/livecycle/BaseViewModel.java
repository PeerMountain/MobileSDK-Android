package com.peermountain.sdk.ui.base.livecycle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.DialogInterface;

import com.peermountain.core.model.guarded.InfoMessage;
import com.peermountain.core.model.guarded.ProgressData;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.utils.LogUtils;

import java.util.HashSet;


/**
 * Created by Galeen on 12/21/2017.
 * implements LiveData for show/hide progress , errors and info messages
 * also provide a Repository object
 * Most ViewModel presenters should extend from this one
 */

public abstract class BaseViewModel extends ViewModel {
    private int currentTaskId = 0;
    public HashSet<MainCallback> callbacks = new HashSet<>();

    private MutableLiveData<ProgressData> showProgressDialog;
    private MutableLiveData<Boolean> showProgressBar;
    private MutableLiveData<Boolean> showNoNetwork;
    private MutableLiveData<NetworkResponse> showError;
    private MutableLiveData<InfoMessage> showMessage;
    private MutableLiveData<NetworkResponse> showErrorDialog;

    protected abstract void init();

    LiveData<ProgressData> shouldShowProgressDialog() {
        if (showProgressDialog == null) {
            showProgressDialog = new MutableLiveData<>();
        }
        return showProgressDialog;
    }

    LiveData<Boolean> shouldShowProgressBar() {
        if (showProgressBar == null) {
            showProgressBar = new MutableLiveData<>();
        }
        return showProgressBar;
    }

    LiveData<NetworkResponse> showErrorDialog() {
        if (showErrorDialog == null) {
            showErrorDialog = new MutableLiveData<>();
        }
        return showErrorDialog;
    }

    LiveData<Boolean> shouldShowNoNetwork() {
        if (showNoNetwork == null) {
            showNoNetwork = new MutableLiveData<>();
        }
        return showNoNetwork;
    }

    LiveData<NetworkResponse> shouldShowError() {
        if (showError == null) {
            showError = new MutableLiveData<>();
        }
        return showError;
    }

    LiveData<InfoMessage> shouldShowMessage() {
        if (showMessage == null) {
            showMessage = new MutableLiveData<>();
        }
        return showMessage;
    }

    public void showError(String msg) {
        showError.setValue(new NetworkResponse(msg));
    }

    public void showError(int msg) {
        showError.setValue(new NetworkResponse(msg));
    }

    public void showErrorDialog(int msg) {
        showErrorDialog.setValue(new NetworkResponse(msg));
    }

    public void setShowErrorDialog(String msg) {
        showErrorDialog.setValue(new NetworkResponse(msg));
    }

    public void showMessage(String msg) {
        showMessage.setValue(msg==null?null:new InfoMessage(msg));
    }

    public void showMessage(int msg) {
        showMessage.setValue(new InfoMessage(msg));
    }

    public BaseEvents getNetworkCallback(){
        return baseEvents;
    }

    private BaseEvents baseEvents = new BaseEvents() {
        @Override
        public void removeCallback(MainCallback callback) {
            callbacks.remove(callback);
        }

        @Override
        public void addCallback(MainCallback callback) {
            currentTaskId++;
            callback.id = currentTaskId;
            callbacks.add(callback);
        }

        @Override
        public void hideProgressBar() {
            showProgressBar.setValue(false);
        }

        @Override
        public void hideProgressDialog() {
            showProgressDialog.postValue(null);
        }

        @Override
        public void showProgressBar() {
            showProgressBar.setValue(true);
        }

        @Override
        public void showProgressDialog(String loadingMsg, DialogInterface.OnCancelListener onCancelListener) {
            showProgressDialog.setValue(new ProgressData(loadingMsg, onCancelListener));
        }

        @Override
        public void showNoNetwork() {
            showNoNetwork.setValue(true);
        }

        @Override
        public void showError(String msg, NetworkResponse networkResponse) {
            showError.setValue(networkResponse);
        }
    };

    @Override
    protected void onCleared() {
        super.onCleared();
        //stop all background processes
        for (MainCallback callback : callbacks) {
            if(callback!=null){
                callback.cancelBackgroundOperation(false);
                    LogUtils.d(this.getClass().getSimpleName(), "cancelBackgroundOperation");
            }
        }
        callbacks.clear();
    }

    public boolean isSameCallRunning(Object callbackToRun) {
        //check if there is no running call already
        for (MainCallback callback : callbacks) {
            if(callbackToRun.getClass().isInstance(callback)) return true;
        }
        return false;
    }
}