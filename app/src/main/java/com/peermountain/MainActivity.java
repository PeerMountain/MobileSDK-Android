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
import android.widget.TextView;

import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.utils.LogUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_USER = "EXTRA_USER";
    private TextView mTvMessage;

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
        mTvMessage.setText("Welcome "+ publicUser.getFirstname());
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
    }
}
