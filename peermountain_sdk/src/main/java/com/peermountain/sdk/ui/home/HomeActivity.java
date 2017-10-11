package com.peermountain.sdk.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarActivity;

public class HomeActivity extends ToolbarActivity {
    private static final int REQUEST_LOGIN = 123;
    private static final int REQUEST_REGISTER = 321;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_home);
        initParentToolbarViews(findViewById(R.id.llMainView));
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        authorize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_REGISTER:
            case REQUEST_LOGIN:
                if (resultCode!=RESULT_OK || !checkUserIsValid()) {
                    finish();
                } else {
                    setUpView();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpView() {

    }

    private boolean checkUserIsValid() {
        return PeerMountainManager.getProfile() != null;
    }

    private void authorize() {
        if (PeerMountainManager.getPin() != null || PeerMountainManager.getFingerprint()) {
            PeerMountainSDK.authorize(this, REQUEST_LOGIN);
        } else
            PeerMountainSDK.registerFlow(this, REQUEST_REGISTER);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
