package com.peermountain.core.network;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;


import java.lang.ref.WeakReference;


/**
 * Created by Galeen on 21.1.2016 г..
 * Implements NetworkOperationCallback, it has all the logic for
 * show/hide progress, error , noNetwork etc.
 * Each caller(ViewModel presenter) can create a inner class which can extend MainCallback and
 * override its methods usually onPostExecute(when is succeeded), onError(when server return 300+)
 * and inTheEndOfDoInBackground(which is called when is succeeded right before onPostExecute and is in the
 * background, so you can parse and persist your data with Repository)
 * Keeps the presenterCallback as WeakReference.
 */
public class MainCallback implements NetworkOperationCallback {
    public static final int TYPE_DIALOG = 273;
    public static final int TYPE_BAR = 272;
    public static final int TYPE_NO_PROGRESS = 271;
    public WeakReference<BaseEvents> presenterCallback;
    private boolean withProgress, cancelable = false, isTaskRunning = false;
    private int progressType;
    private String loadingMsg;
    private AsyncTask task;
    public int id;
    /**
     * @param presenterCallback     extend BaseActivity which will make sure to show/hide progress
     * @param progressType should be one of MainCallback.TYPE_DIALOG , MainCallback.TYPE_BAR or MainCallback.TYPE_NO_PROGRESS
     */
    public MainCallback(BaseEvents presenterCallback, int progressType) {
        this(presenterCallback, progressType, false, null);
    }

    public MainCallback(BaseEvents presenterCallback, int progressType, boolean cancelable, String loadingMsg) {
        this.progressType = progressType;
        this.cancelable = cancelable;
        this.loadingMsg = loadingMsg;
        withProgress = progressType != TYPE_NO_PROGRESS;
        this.presenterCallback = new WeakReference<>(presenterCallback);
    }

    /**
     * Cancel the operation, instead of onPostExecute , onCancel will be notified
     *
     * @param mayInterruptIfRunning True if the thread executing this task should be interrupted; otherwise, in-progress tasks are allowed to complete.
     * If you want to let the service to get the data from the service and pass it to inTheEndOfDoInBackground use false.
     *
     * @return False if the task could not be cancelled, typically because it has already completed normally; true otherwise
     */
    public boolean cancelBackgroundOperation(boolean mayInterruptIfRunning) {
        return task != null && task.cancel(mayInterruptIfRunning);
    }

    private boolean checkPresenterIsAlive() {
        return presenterCallback.get() != null;// && !presenterCallback.get().isFinishing();
    }

    private void showProgress() {
        if (checkPresenterIsAlive()) {
            switch (progressType) {
                case TYPE_DIALOG:
                    if (cancelable) {
                        presenterCallback.get().showProgressDialog(loadingMsg, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                cancelBackgroundOperation(false);
                            }
                        });
                    } else {
                        presenterCallback.get().showProgressDialog(loadingMsg, null);
                    }
                case TYPE_BAR:
                    presenterCallback.get().showProgressBar();
            }
        }
    }

    private void hideProgress() {
        if (checkPresenterIsAlive()) {
            switch (progressType) {
                case TYPE_DIALOG:
                    presenterCallback.get().hideProgressDialog();
                case TYPE_BAR:
                    presenterCallback.get().hideProgressBar();
            }
        }
    }

    @Override
    public void onPreExecute() {
        if (withProgress) {
            showProgress();
        }
        isTaskRunning = true;
    }

    @Override
    public void onPostExecute(NetworkResponse networkResponse) {
        if (withProgress) {
            hideProgress();
        }
        removeCallback();
        isTaskRunning = false;
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onCancel() {
        if (withProgress) {
            hideProgress();
        }
        removeCallback();
        isTaskRunning = false;
    }

    @Override
    public void onError(String msg, NetworkResponse networkResponse) {
        if (networkResponse != null)
            Log.e("Error", "message : " + msg + "\nresponse : " + networkResponse.responseCode + " " + networkResponse.json + " " + networkResponse.error);
        else
            Log.e("Error", "message : " + msg);

        showError(msg, networkResponse);
        isTaskRunning = false;
    }

    protected void showError(String msg, NetworkResponse networkResponse) {
        if (withProgress) {
            hideProgress();
        }
        if (checkPresenterIsAlive()) {
            presenterCallback.get().showError(msg, networkResponse);
        }
        removeCallback();
    }

    @Override
    public void onOldData(NetworkResponse networkResponse) {

    }

    @Override
    public void onNoNetwork() {
        if (checkPresenterIsAlive()) {
            presenterCallback.get().showNoNetwork();
        }
        removeCallback();
        isTaskRunning = false;
    }

    @Override
    public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {

    }

    /**
     * removes this callback from presenterCallback's callbacks set as is done
     */
    private void removeCallback(){
        if(checkPresenterIsAlive()){
            presenterCallback.get().removeCallback(this);
        }
    }

    void setTask(AsyncTask task) {
        this.task = task;
    }

    public boolean isTaskRunning() {
        return isTaskRunning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MainCallback)) return false;

        MainCallback that = (MainCallback) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
