package com.peermountain.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.peermountain.core.model.PmAccessToken;
import com.peermountain.core.model.PublicUser;
import com.peermountain.core.persistence.PersistenceManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;


public class LoginActivity extends AppCompatActivity {

    public static void show(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_login);
        getViews();
        setListeners();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    FrameLayout flLoginBtn;

    private void getViews() {
        flLoginBtn = findViewById(R.id.pm_flLoginLinkedInBtn);
    }


    private void setListeners() {
        flLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToLI();
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
                        PersistenceManager.saveLiAccessToken(new PmAccessToken(accessToken.getValue(), accessToken.getExpiresOn()));
                        getLiUser();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        DialogUtils.showErrorToast(LoginActivity.this, getString(R.string.pm_error_msg, error.toString()));
                        LogUtils.e("LISessionManager", getString(R.string.pm_error_msg, error.toString()));
                        finish();
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
                PublicUser liUser = PersistenceManager.saveLiUser(s.getResponseDataAsString());
                Intent data = new Intent();
                data.putExtra(PeerMountainSdkConstants.EXTRA_PUBLIC_USER,liUser);
                setResult(RESULT_OK,data);
                finish();
            }

            @Override
            public void onApiError(LIApiError error) {
                LogUtils.e("getLiUser", error.toString());
                DialogUtils.showErrorToast(LoginActivity.this, getString(R.string.pm_error_msg, error.toString()));
                finish();
            }
        });
    }
}
