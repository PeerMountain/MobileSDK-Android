package com.peermountain.pm_net.network.teleferique.callbacks;

import com.peermountain.pm_net.network.BaseEvents;
import com.peermountain.pm_net.network.MainCallback;
import com.peermountain.pm_net.network.NetworkResponse;
import com.peermountain.pm_net.network.teleferique.TeleferiqueJsonParser;

import java.io.IOException;

/**
 * Created by Galeen on 4/2/18.
 */
public class TimeCallback extends MainCallback {
    private Events callback;

    public TimeCallback(BaseEvents presenterCallback, int progressType, Events callback) {
        super(presenterCallback, progressType);
        this.callback = callback;
    }

    @Override
    public void onNoNetwork() {
        super.onNoNetwork();
        if(callback!=null) callback.onNoNetwork();
    }

    @Override
    public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
        super.inTheEndOfDoInBackground(networkResponse);
        try {
            networkResponse.object = TeleferiqueJsonParser.readServerTime(networkResponse.json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostExecute(NetworkResponse networkResponse) {
        super.onPostExecute(networkResponse);
        if(callback!=null) {
            if (networkResponse.object != null) {
                callback.onTime((String) networkResponse.object);
            }else{
                callback.onTime(null);
            }
        }
    }

    @Override
    public void onError(String msg, NetworkResponse networkResponse) {
        super.onError(msg, networkResponse);
        if(callback!=null) callback.onError(msg, networkResponse);
    }

    public static abstract class Events extends MainCallback{

        public Events(BaseEvents presenterCallback, int progressType) {
            super(presenterCallback, progressType);
        }

        public abstract void onTime(String time);
    }
}
