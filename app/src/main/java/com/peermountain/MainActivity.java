package com.peermountain;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;
import com.peermountain.ui.StartActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SCAN_ID = 493;
    private static final String EXTRA_USER = "EXTRA_USER";
    private TextView mTvMessage;
    private Button mBtnScanId;
    private Button mBtnLogout;

    public static void show(Context context, PublicUser publicUser) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(EXTRA_USER, publicUser);
        context.startActivity(starter);
    }

    PublicUser publicUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        publicUser = getIntent().getParcelableExtra(EXTRA_USER);
        if (publicUser == null) {
            LogUtils.e("MainActivity.onCreate", "No user object!");
            finish();
            return;
        }

        if (BuildConfig.DEBUG) {
            printKeyHash();
        }

        initView();
        setListeners();

        mTvMessage.setText("Welcome " + publicUser.getFirstname());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCAN_ID:
                if(resultCode == RESULT_OK && data !=null && data.getExtras()!=null){
                    String idNumber = data.getExtras().getString(PeerMountainSdkConstants.EXTRA_ID_NUMBER,null);
                    if(idNumber!=null){
                        mTvMessage.setText(publicUser.getFirstname()+
                        ", your id number is "+idNumber);
                    }else {
                        mTvMessage.setText(publicUser.getFirstname() +
                                " your id number is not scanned");
                    }
                }
                break;
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("MY KEY HASH:", "Error : " + e.getMessage());
        }
    }

    private void initView() {
        mTvMessage = (TextView) findViewById(R.id.tvMessage);
        mBtnScanId = (Button) findViewById(R.id.btnScanId);
        mBtnLogout = (Button) findViewById(R.id.btnLogout);
    }

    private void setListeners() {
        mBtnScanId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeerMountainSDK.scanID(MainActivity.this, REQUEST_SCAN_ID);
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeerMountainSDK.logout();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
            }
        });
    }
}
