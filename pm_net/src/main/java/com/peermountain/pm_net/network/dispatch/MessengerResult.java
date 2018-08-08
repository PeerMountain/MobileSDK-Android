package com.peermountain.pm_net.network.dispatch;

/**
 * Created by Galeen on 11.7.2017 г..
 */

public class MessengerResult {
    public boolean success;
    public Object object;

    MessengerResult(boolean success, Object object) {
        this.success = success;
        this.object = object;
    }
}
