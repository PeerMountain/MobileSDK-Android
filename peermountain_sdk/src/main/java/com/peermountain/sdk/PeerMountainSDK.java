package com.peermountain.sdk;

import android.app.Activity;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.sdk.ui.LoginActivity;
import com.peermountain.sdk.ui.authorized.HomeActivity;
import com.peermountain.sdk.ui.register.RegisterActivity;

/**
 * Created by Galeen on 10/2/17.
 * This is a manager class to execute single feature as activity or a flow as stack
 */

public class PeerMountainSDK {
    /**
     * This method must be called in Application.onCreate
     * @param config PeerMountainConfig
     */
    public static void init(PeerMountainConfig config){
        PeerMountainManager.init(config);
    }

    public static void authorize(Activity callerActivity, int requestCode){
        LoginActivity.show(callerActivity,requestCode);
    }

    public static void registerFlow(Activity callerActivity, int requestCode){
       callerActivity.startActivityForResult(new Intent(callerActivity, RegisterActivity.class),requestCode);
    }

    public static void goHome(Activity callerActivity){
        callerActivity.startActivity(new Intent(callerActivity, HomeActivity.class));
    }

    public static void logout(){
        PeerMountainManager.logoutPublicProfile();
        LoginManager.getInstance().logOut();
    }

    public static void resetProfile(){
        if(PeerMountainManager.getApplicationContext()!=null){
            PmCoreUtils.deleteFiles(PeerMountainManager.getApplicationContext());
        }
        PeerMountainManager.resetProfile();
        LoginManager.getInstance().logOut();
    }
}
