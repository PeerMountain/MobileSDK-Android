package com.peermountain.id_demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.peermountain.common.model.DocumentID;
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.scan_id_sdk.scan.ScanIdentityDocumentCameraActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ID = 111;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ID && data!=null) {
            DocumentID documentID = data.getParcelableExtra(ScanIdentityDocumentCameraActivity.ID);
            if(documentID != null){
                PmDialogUtils.showChoiceDialog(this, null,
                        getString(R.string.dialog_ask_for_selfie_msg, documentID.getFirstName()),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
    }
}
