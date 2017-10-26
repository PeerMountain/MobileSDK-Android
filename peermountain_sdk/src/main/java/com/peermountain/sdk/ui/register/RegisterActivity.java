package com.peermountain.sdk.ui.register;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.SecureActivity;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;

public class RegisterActivity extends SecureActivity implements RegisterPinFragment.OnFragmentInteractionListener,
        RegisterKeywordsFragment.OnFragmentInteractionListener, ScanIdFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener, RegisterProfileFragment.OnFragmentInteractionListener, IntroFragment.OnFragmentInteractionListener, RegisterSelectKeywordsFragment.OnFragmentInteractionListener,
ConfirmationAccountFragment.OnFragmentInteractionListener, SecurityTutorialFragment.OnFragmentInteractionListener{
    @IdRes
    int containerId = R.id.flContainer;
    PmFragmentUtils.FragmentBuilder fb;
    ScanIdFragment scanIdFragment;
    DocumentID scannedDocument = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_register,R.id.llMainView);
        fb = PmFragmentUtils.init(this, containerId);
        if(savedInstanceState!=null && scannedDocument!=null){
            showRegisterProfileFragment(scannedDocument);
        }else {
            if (PeerMountainManager.isTutoSeen()) {
                showPinFragment();
            } else {
                showTutorialFragment();
            }
            setResult(Activity.RESULT_CANCELED);
        }
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
        if(requestCode==1) {
            final boolean permissionsAllowed =
                    AXTCaptureInterface.INSTANCE.verifyPermissions(requestCode, permissions, grantResults) && ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED;
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
    }

    private boolean handleOnBack() {
        return topFragment != null && topFragment.onBackPressed();
    }

    private void showPinFragment() {
        fb.addToBackStack(false);
        fb.replace(RegisterPinFragment.newInstance(false));
    }

    private void showTutorialFragment() {
        fb.addToBackStack(false);
        fb.replace(new IntroFragment());
    }
    private void showKeywordsFragment() {
//        showRegisterProfileFragment(null);
        fb.addToBackStack(true);
        fb.replace(RegisterSelectKeywordsFragment.newInstance(false));
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

    private void showRegisterProfileFragment(DocumentID document) {
        fb.addToBackStack(false);
        fb.replace(RegisterProfileFragment.newInstance(document));
    }
    private void showConfirmationFragment() {
        fb.addToBackStack(true);
        fb.replace(new ConfirmationAccountFragment());
    }
    private void showSecurityTutorialFragment() {
        fb.addToBackStack(true);
        fb.replace(new SecurityTutorialFragment());
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
    public void onWhy() {
        showSecurityTutorialFragment();
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
    public void onScannedIdDataAccepted(DocumentID scannedDocument) {
        showRegisterProfileFragment(scannedDocument);
    }

    @Override
    public void onProfileRegistered() {
        showConfirmationFragment();
    }

    @Override
    public void onTutoEnd() {
        showPinFragment();
    }

    @Override
    public void onSecurityTutorialEnd() {
        fb.pop();
    }

    @Override
    public void onAccountCreated() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void lockMenu(boolean lock) {

    }
}
