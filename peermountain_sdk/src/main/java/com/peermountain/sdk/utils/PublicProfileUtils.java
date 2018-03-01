package com.peermountain.sdk.utils;

import android.content.Context;
import android.os.Bundle;

import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.utils.Scope;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.sdk.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Galeen on 11/1/2017.
 */

public class PublicProfileUtils {
    public static void getFbUser(LoginResult loginResult, GraphRequest.GraphJSONObjectCallback callback) {
        if (loginResult.getAccessToken() != null) {
            final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),callback );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,picture");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public static void getLiUser(final Context appContext, ApiListener callback) {
        APIHelper apiHelper = APIHelper.getInstance(appContext);
        apiHelper.getRequest(appContext, PmCoreConstants.GET_LI_PROFILE,callback);
    }

    public static void onLoginError(Context context,String errorMsg) {
        DialogUtils.showErrorToast(context, context.getString(R.string.pm_error_msg, errorMsg));
    }

    public static Scope buildScopeLn() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }
    public static List<String> fbPermissions() {
        return  Arrays.asList("public_profile", "email");
    }
}
