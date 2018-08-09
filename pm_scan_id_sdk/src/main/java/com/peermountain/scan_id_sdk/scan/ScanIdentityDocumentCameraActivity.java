package com.peermountain.scan_id_sdk.scan;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.peermountain.common.model.DocumentID;
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.common.utils.PmFragmentUtils;
import com.peermountain.pm_livecycle.base.BaseActivity;
import com.peermountain.scan_id_sdk.R;
import com.peermountain.scan_id_sdk.show_data.ShowScannedIdFragment;

import java.io.File;
import java.util.ArrayList;

public class ScanIdentityDocumentCameraActivity extends BaseActivity<IdentityDocumentViewModel>
        implements ScanDocumentFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener {
    public static final String ID = "ID";
    private static final String ARG_WITH_PREVIEW = "withPreview";

    private boolean withPreview = false;
    private ScanDocumentFragment scanDocumentFragment;
    private PmFragmentUtils.FragmentBuilder fragmentBuilder;

    public static void show(Activity activity, int requestCode, boolean withPreview) {
        Intent starter = new Intent(activity, ScanIdentityDocumentCameraActivity.class);
        starter.putExtra(ARG_WITH_PREVIEW, withPreview);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.pm_activity_camera_id);

        setResult(RESULT_CANCELED);

        withPreview = getIntent().getBooleanExtra(ARG_WITH_PREVIEW, false);
        fragmentBuilder = PmFragmentUtils.init(this, R.id.flFragmentContainer);

        if (savedInstanceState == null) {
            fragmentBuilder.addToBackStack(false)
                    .replace(scanDocumentFragment = ScanDocumentFragment.newInstance());
        }
    }

    @Override
    protected void setObservers() {
        activityViewModel.getOnDocumentScannedFromServer().observe(this, new Observer<DocumentID>() {
            @Override
            public void onChanged(@Nullable DocumentID documentID) {
                handleDocumentResult(documentID);
            }
        });
    }

    @NonNull
    @Override
    protected Class<IdentityDocumentViewModel> getViewModel() {
        return IdentityDocumentViewModel.class;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (scanDocumentFragment != null)
            scanDocumentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onID_Ready(ArrayList<File> files) {
        activityViewModel.sendFiles(files);
    }

    @Override
    public void onPermissionRefused() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onScannedIdDataAccepted(DocumentID scannedDocument) {
        Intent intent = new Intent();
        intent.putExtra(ID, scannedDocument);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScannedIdDataRejected(DocumentID scannedDocument) {
        fragmentBuilder.pop();
    }

    private void handleDocumentResult(@Nullable DocumentID documentID) {
        if (checkDocumentIsValid(documentID)) {
            onValidDocument(documentID);
        }
    }

    private boolean checkDocumentIsValid(@Nullable DocumentID documentID) {
        if (documentID == null || documentID.getErrorMessage() != null
                || (!withPreview && !documentID.checkIsValid())) {
            String msg;
            if (documentID != null) {
                documentID.deleteDocumentImages();
                msg = documentID.getErrorMessage();
            } else {
                msg = getString(R.string.pm_err_msg_scan_data);
            }
            PmDialogUtils.showErrorToast(ScanIdentityDocumentCameraActivity.this, msg);
            if (scanDocumentFragment != null) scanDocumentFragment.resetScanning();
            return false;
        }
        return true;
    }

    private void onValidDocument(DocumentID documentID) {
        Intent intent = new Intent();
        if (!withPreview) {// return document to caller
            intent.putExtra(ID, documentID);
            setResult(RESULT_OK, intent);
            finish();
        } else {// show data and ask for acceptance
            intent.putExtra(ShowScannedIdFragment.ID, documentID);
            fragmentBuilder.addToBackStack(true)
                    .replace(ShowScannedIdFragment.newInstance(intent));
        }
    }
}
