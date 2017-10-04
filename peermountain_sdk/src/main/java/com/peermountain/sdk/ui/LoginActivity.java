package com.peermountain.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    public static void show(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }


    private CallbackManager callbackManager;
    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.pm_activity_login);
        getViews();
        setListeners();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    FrameLayout flLoginLN, flLoginFB;

    private void getViews() {
        flLoginLN = findViewById(R.id.pm_flLoginLinkedInBtn);
        flLoginFB = findViewById(R.id.pm_flLoginWithFB);
    }


    private void setListeners() {
        flLoginLN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToLI();
            }
        });
        flLoginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                loginToFb();
            }
        });
    }

    private void loginToLI() {
        LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new
                AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        AccessToken accessToken = LISessionManager.getInstance
                                (getApplicationContext()).getSession().getAccessToken();
                        PeerMountainManager.saveLiAccessToken(new PmAccessToken(accessToken.getValue(), accessToken.getExpiresOn()));
                        getLiUser();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        LogUtils.e("LISessionManager", getString(R.string.pm_error_msg, error.toString()));
                        onLoginError(error.toString());
                    }
                }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private void getLiUser() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, PeerMountainCoreConstants.GET_LI_PROFILE, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse s) {
                LogUtils.d("getLiUser", s.toString());
                PublicUser liUser = PeerMountainManager.saveLiUser(s.getResponseDataAsString());
                returnUser(liUser);
            }

            @Override
            public void onApiError(LIApiError error) {
                LogUtils.e("getLiUser", error.toString());
                onLoginError( error.toString());
            }
        });
    }

    private void returnUser(PublicUser liUser) {
        Intent data = new Intent();
        data.putExtra(PeerMountainSdkConstants.EXTRA_PUBLIC_USER, liUser);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onLoginError(String errorMsg) {
        DialogUtils.showErrorToast(LoginActivity.this, getString(R.string.pm_error_msg, errorMsg));
        finish();
    }

    public void loginToFb() {
        manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFbUser(loginResult);
            }

            @Override
            public void onCancel() {
                LogUtils.e("FacebookException", "onCancel");
                onLoginError(getString(R.string.pm_error_canceled));
            }

            @Override
            public void onError(FacebookException exception) {
                LogUtils.e("FacebookException", exception.getMessage());
                onLoginError(exception.getMessage());
            }
        });
        List<String> permissionNeeds = Arrays.asList("public_profile", "email");
        manager.logInWithReadPermissions(this, permissionNeeds);
    }

    private void getFbUser(LoginResult loginResult) {
        if (loginResult.getAccessToken() != null) {
            final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject userJ, GraphResponse response) {
                    PublicUser publicUser = parseFbUser(userJ);
                    PeerMountainManager.saveLiUser(publicUser);
                    returnUser(publicUser);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,picture");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private PublicUser parseFbUser(JSONObject userJ) {
        PublicUser publicUser = null;
        if (userJ != null) {
            LogUtils.d("fb user",userJ.toString());
            if (!userJ.optString("id").isEmpty()) {
                String id = userJ.optString("id");
                String name = userJ.optString("first_name");
                String lastName = userJ.optString("last_name");
                String email = userJ.optString("email");
                String gender = userJ.optString("gender");
                String picture = userJ.optString("picture");
                if (picture != null) {
                    JSONObject picObj = userJ.optJSONObject("picture");
                    if (picObj != null && picObj.has("data")) {
                        picture = picObj.optString("url");
                    } else {
                        picture = null;
                    }
                }
                publicUser = new PublicUser(id, email, name, lastName, picture);
                Toast.makeText(LoginActivity.this, "FB logged", Toast.LENGTH_LONG).show();
            }
        }
        return publicUser;
    }
}
