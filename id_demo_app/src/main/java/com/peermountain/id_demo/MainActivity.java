package com.peermountain.id_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.peermountain.scan_id_sdk.scan.ScanIdentityDocumentCameraActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnStartIdScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanIdentityDocumentCameraActivity.show(MainActivity.this);
            }
        });

    }
}
