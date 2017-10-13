package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.peermountain.core.model.guarded.Document;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarActivity;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;

public class RegisterActivity extends ToolbarActivity implements RegisterPinFragment.OnFragmentInteractionListener,
        RegisterKeywordsFragment.OnFragmentInteractionListener, ScanIdFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener, RegisterProfileFragment.OnFragmentInteractionListener {
    @IdRes
    int containerId = R.id.flContainer;
    PmFragmentUtils.FragmentBuilder fb;
    ScanIdFragment scanIdFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_register,R.id.llMainView);
        fb = PmFragmentUtils.init(this, containerId);
        showPinFragment();
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    public void onBackPressed() {
        if (!handleOnBack()) super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(topFragment!=null && topFragment instanceof RegisterProfileFragment){
            topFragment.onActivityResult(requestCode, resultCode, data);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final boolean permissionsAllowed =
                AXTCaptureInterface.INSTANCE.verifyPermissions(requestCode, permissions, grantResults);
        if (permissionsAllowed) {
            initScanIdSDK();
        } else {
            DialogUtils.showChoiceDialog(this, -1, R.string.pm_err_msg_permission_scan_id,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initScanIdSDK();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, R.string.pm_btn_ask_for_permission_again, R.string.btn_refuse_permission);
        }
    }

    private boolean handleOnBack() {
        return topFragment != null && topFragment.onBackPressed();
    }

    private void showPinFragment() {
        fb.addToBackStack(false);
        fb.replace(RegisterPinFragment.newInstance(false));
    }

    private void showKeywordsFragment() {
        fb.addToBackStack(true);
        fb.replace(RegisterKeywordsFragment.newInstance());
    }

    private void showScanIdFragment() {
//        registerKeywordsFragment = null;
        fb.addToBackStack(true);
        fb.replace(scanIdFragment = ScanIdFragment.newInstance(null, null));
    }

    private void showScannedIdDataFragment(Intent scannedData) {
        scanIdFragment = null;
        fb.addToBackStack(true);
        fb.replace(ShowScannedIdFragment.newInstance(scannedData));
    }

    private void showRegisterProfileFragment(Document document) {
        fb.addToBackStack(true);
        fb.replace(RegisterProfileFragment.newInstance(document));
    }

    @Override
    public void goToRegisterKeyWords() {
        showKeywordsFragment();
    }

    @Override
    public void onLogin() {

    }

    @Override
    public void onLoginCanceled() {

    }

    @Override
    public void onKeywordsSaved() {
        showScanIdFragment();
    }

    @Override
    public void initScanIdSDK() {
        PeerMountainManager.initScanSDK(this, new AXTCaptureInterfaceCallback() {
            @Override
            public void onInitSuccess() {
                if (scanIdFragment != null) scanIdFragment.onScanSDKEnabled(true);
            }

            @Override
            public void onInitError() {
                if (scanIdFragment != null) scanIdFragment.onScanSDKEnabled(false);
            }
        });
    }

    @Override
    public boolean isScanSDKReady() {
        return PeerMountainManager.isScanIdSDKReady();
    }

    @Override
    public void onIdScanned(Intent scannedData) {
        showScannedIdDataFragment(scannedData);
    }

    @Override
    public void onScannedIdDataAccepted(Document scannedDocument) {
        showRegisterProfileFragment(scannedDocument);
    }

    @Override
    public void onProfileRegistered() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
