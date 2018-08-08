package com.peermountain.sdk.ui.base;

import android.os.Bundle;
import android.view.WindowManager;

import com.peermountain.core.persistence.InactiveTimer;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.pm_livecycle.base.BaseViewModel;

/**
 * Created by Galeen on 12.7.2017 г..
 */

public abstract class SecureActivity<T extends BaseViewModel> extends ToolbarActivity<T> {
    public InactiveTimer.InactiveTimerInteractions inactivityCallback = null;
//    public abstract void initInactivityCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }


//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        if(inactivityCallback!=null) {
//            InactiveTimer.startListeningForNewInactivity();
//            PeerMountainManager.saveLastTimeActive();
//        }
////        LogUtils.w("Secure atv", "onUserInteraction");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        startListenForUserInteraction();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopListenForUserInteraction();
//    }

    public void stopListenForUserInteraction() {
        if(inactivityCallback!=null) {
            InactiveTimer.stopListening();
            InactiveTimer.removeListener(inactivityCallback);
            inactivityCallback = null;
        }
    }

    public void startListenForUserInteraction() {
        if(inactivityCallback!=null) {
            InactiveTimer.addListener(inactivityCallback);
            InactiveTimer.startListeningForNewInactivity();
            PeerMountainManager.saveLastTimeActive();
        }
    }
}
