package com.peermountain.core.persistence;

import android.os.Handler;

import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Galeen on 20.6.2016 г..
 */
public class InactiveTimer {
    private static final String TAG = "InactiveTimer";
    private static Timer timer = null;
    private static Handler myHandler = new Handler();
    private static HashSet<InactiveTimerInteractions> listeners;
    private static InactiveTimerInteractions listener;

//    private static final Object lock = new Object();
//    private static InactiveTimer instance;
//    private InactiveTimer(){
//        instance = this;
//    }

//    public static InactiveTimer getInstance(){
//        if(instance==null) {
//            synchronized (lock) {
//                instance = new InactiveTimer();
//            }
//        }
//        return instance;
//    }

    public static void startListeningForNewInactivity() {
        stopListening();
        log("Start "+ new Date(System.currentTimeMillis()).toString());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                myHandler.post(new Runnable() {
                    public void run() {
                        log("End "+ new Date(System.currentTimeMillis()).toString());
                        if (listener != null) {
//                            for (InactiveTimerInteractions listener : listeners) {
                                listener.onTimeOfInactivityEnds();
//                            }
//                            if(listeners.size()==0) LogUtils.d(TAG,"no callbacks");
                        }
                    }
                });
            }
        }, getSessionTime()/* amount of time in milliseconds before execution */);
    }

    private static int getSessionTime() {
        return 30 * 1000;//10 min
    }

    public static void stopListening() {
        if (timer != null) {
            timer.cancel();
            log("Stop "+new Date(System.currentTimeMillis()).toString());
        }
    }

    public static void addListener(InactiveTimerInteractions listener){
//        if(listeners==null) listeners = new HashSet<>();
//        listeners.add(listener);
        InactiveTimer.listener = listener;
        log("Add listener");
    }

    public static void removeListener(InactiveTimerInteractions listener){
//        if(listeners!=null) {
//            listeners.remove(listener);
//            LogUtils.d(TAG, "Remove listener");
//        }
        InactiveTimer.listener = null;
        log("Remove listener");
    }
    private static void log(String msg){
//        LogUtils.d(TAG, msg);
    }

    public abstract static class InactiveTimerInteractions {
        public abstract void onTimeOfInactivityEnds();
    }
}
