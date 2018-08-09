package com.peermountain.id_demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.peermountain.common.model.DocumentID;
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.pm_scan_selfie_sdk.CameraLiveSelfieActivity;
import com.peermountain.scan_id_sdk.scan.ScanIdentityDocumentCameraActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ID = 111;
    private static final int REQUEST_CODE_SELFIE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartIdScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanIdentityDocumentCameraActivity.show(MainActivity.this, REQUEST_CODE_ID, true);
            }
        });
    }

    private DocumentID documentID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_ID:
                    getDocument(data);
                    break;
                case REQUEST_CODE_SELFIE:
                    getLiveSelfie(data);
                    break;
            }

        }
    }

    private void getDocument(Intent data) {
        documentID = data.getParcelableExtra(ScanIdentityDocumentCameraActivity.ID);
        if (documentID != null) {
            showStartLiveSelfieDialog(R.string.dialog_ask_for_selfie_msg);
        }
    }

    private void getLiveSelfie(Intent data) {
        ArrayList<String> liveSelfieUris = data.getStringArrayListExtra(CameraLiveSelfieActivity.ARG_LIVE_SELFIE_URIS);
        if (liveSelfieUris != null && liveSelfieUris.size() > 0) {
            PmDialogUtils.showSimpleDialog(this,
                    getString(R.string.dialog_selfie_received_msg, documentID.getFirstName(), liveSelfieUris.size()),
                    null);
        } else {
            showStartLiveSelfieDialog(R.string.dialog_ask_for_selfie_err_msg);
        }
    }

    private void showStartLiveSelfieDialog(@StringRes int msg) {
        PmDialogUtils.showChoiceDialog(this, null,
                getString(msg, documentID.getFirstName()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CameraLiveSelfieActivity.show(MainActivity.this,
                                documentID.getImageCropped().takeImageAsFile(),
                                REQUEST_CODE_SELFIE);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                },
                getString(R.string.dialog_ask_for_selfie_btn_yes), getString(R.string.dialog_ask_for_selfie_btn_no));
    }
}
