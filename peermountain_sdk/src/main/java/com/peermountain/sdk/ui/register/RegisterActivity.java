package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.core.utils.constants.PeerMountainCoreConstants;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.SecureActivity;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;

public class RegisterActivity extends SecureActivity<RegisterViewModel> implements RegisterPinFragment.OnFragmentInteractionListener,
        ScanIdFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener, RegisterProfileFragment.OnFragmentInteractionListener, IntroFragment.OnFragmentInteractionListener, RegisterSelectKeywordsFragment.OnFragmentInteractionListener,
        ConfirmationAccountFragment.OnFragmentInteractionListener, SecurityTutorialFragment.OnFragmentInteractionListener {

    public static final int REQUEST_ID_CAPTURE = 111;
    @IdRes
    int containerId = R.id.flContainer;
    PmFragmentUtils.FragmentBuilder fragmentBuilder;
    ScanIdFragment scanIdFragment;
    DocumentID scannedDocument = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_register, R.id.llMainView);
        fragmentBuilder = PmFragmentUtils.init(this, containerId);
        if (savedInstanceState != null && scannedDocument != null) {
            showRegisterProfileFragment(scannedDocument);
        } else {
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
        if (topFragment != null && topFragment instanceof RegisterProfileFragment) {
            topFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            activityViewModel.checkIdCapture(requestCode, resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Boolean permissionsForScan = PmDocumentsHelper.checkPermissionsForScanId(this, requestCode, permissions, grantResults);
        if (permissionsForScan == null) {//it wasn't meant for it

        } else if (permissionsForScan) {
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

    @Override
    protected void setObservers() {
        activityViewModel.getOnDocumentScannedFromServer().observe(this,
                new Observer<DocumentID>() {
                    @Override
                    public void onChanged(@Nullable DocumentID documentID) {
                        if (documentID == null || !documentID.isValid()) {
                            if(documentID!=null) documentID.deleteDocumentImages();
                            DialogUtils.showErrorToast(RegisterActivity.this, getString(R.string.pm_err_server_data_extraction));
                            showScanIdFragment();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(ShowScannedIdFragment.ID, documentID);
                        onIdScanned(intent);
                    }
                });
    }

    @NonNull
    @Override
    protected Class getViewModel() {
        return RegisterViewModel.class;
    }

    private boolean handleOnBack() {
        return topFragment != null && topFragment.onBackPressed();
    }

    private void showPinFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(RegisterPinFragment.newInstance(false));
    }

    private void showTutorialFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(new IntroFragment());
    }

    private void showKeywordsFragment() {
//        showRegisterProfileFragment(null);
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(RegisterSelectKeywordsFragment.newInstance(false));
    }

    private void showScanIdFragment() {
//        registerKeywordsFragment = null;
//        fragmentBuilder.addToBackStack(true);
//        fragmentBuilder.replace(scanIdFragment = ScanIdFragment.newInstance(null, null));

        CameraActivity.show(this, true, REQUEST_ID_CAPTURE);
    }

    private void showScannedIdDataFragment(Intent scannedData) {
        scanIdFragment = null;
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(ShowScannedIdFragment.newInstance(scannedData));
    }

    private void showRegisterProfileFragment(DocumentID document) {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(RegisterProfileFragment.newInstance(document));
    }

    private void showConfirmationFragment() {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(new ConfirmationAccountFragment());
    }

    private void showSecurityTutorialFragment() {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(new SecurityTutorialFragment());
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
        if (PeerMountainCoreConstants.isFake) {
            showScannedIdDataFragment(null);
        } else {
            showScanIdFragment();
        }
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
    public void onScannedIdDataRejected(DocumentID scannedDocument) {
        fragmentBuilder.pop();
        showScanIdFragment();
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
        fragmentBuilder.pop();
    }

    @Override
    public void onAccountCreated() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void lockMenu(boolean lock) {

    }

    GoogleApiClient mGoogleApiClient;

    @Override
    public GoogleApiClient getGoogleApiClientForSignIn(GoogleSignInOptions gso) {
        if (mGoogleApiClient == null) {
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(RegisterActivity.this, "Unable to connect to Google Api", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return mGoogleApiClient;
    }
}
