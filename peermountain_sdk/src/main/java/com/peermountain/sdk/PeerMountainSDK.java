package com.peermountain.sdk;

import android.app.Activity;

import com.facebook.login.LoginManager;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.ui.LoginActivity;

/**
 * Created by Galeen on 10/2/17.
 * This is a manager class to execute single feature as activity or a flow as stack
 */

public class PeerMountainSDK {

    public static void authorize(Activity callerActivity, int requestCode){
        LoginActivity.show(callerActivity,requestCode);
    }

    public static void logout(){
        PeerMountainManager.logout();
        LoginManager.getInstance().logOut();
    }

    public static void scanID(Activity callerActivity, int requestCode){

    }
}
