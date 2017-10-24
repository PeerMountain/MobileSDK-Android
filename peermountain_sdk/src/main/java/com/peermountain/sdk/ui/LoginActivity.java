package com.peermountain.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarActivity;
import com.peermountain.sdk.ui.register.RegisterPinFragment;
import com.peermountain.sdk.utils.PmFragmentUtils;


public class LoginActivity extends ToolbarActivity implements RegisterPinFragment.OnFragmentInteractionListener{

    public static void show(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_login,R.id.llMain);
        showPinFragment();
        setResult(Activity.RESULT_CANCELED);
   }

    private void showPinFragment() {
        PmFragmentUtils.FragmentBuilder fb = new PmFragmentUtils.FragmentBuilder(this).containerId(R.id.flContainer);
        fb.addToBackStack(false);
        fb.replace(RegisterPinFragment.newInstance(true));
    }

    @Override
    public void goToRegisterKeyWords() {

    }

    @Override
    public void onLogin() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onLoginCanceled() {
        finish();
    }

    @Override
    public void lockMenu(boolean lock) {

    }
}
