package com.peermountain.sdk.ui.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;

public class RegisterActivity extends AppCompatActivity implements
        ToolbarFragment.ToolbarEvents, RegisterPinFragment.OnFragmentInteractionListener,
        RegisterKeywordsFragment.OnFragmentInteractionListener, ScanIdFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener {
    @IdRes
    int containerId = R.id.flContainer;
    PmFragmentUtils.FragmentBuilder fb;
    ScanIdFragment scanIdFragment;

    ToolbarFragment topFragment;
    LinearLayout llMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_register);
        fb = PmFragmentUtils.init(this, containerId);
        showPinFragment();
//        showKeywordsFragment();
        getViews();
    }

    @Override
    public void onBackPressed() {
        if (!handleOnBack()) super.onBackPressed();
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
                    },R.string.btn_ask_for_permission_again,R.string.btn_refuse_permission);
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

    @Override
    public void goToRegisterKeyWords() {
        showKeywordsFragment();
    }

    @Override
    public void onLogin() {

    }

    ImageView pmMenuLeft;
    TextView pmToolbarTitle;

    private void getViews() {
        pmMenuLeft = findViewById(R.id.pmMenuLeft);
        pmToolbarTitle = findViewById(R.id.pmToolbarTitle);
        llMainView = findViewById(R.id.llMainView);
//        RippleUtils.setRippleEffectSquare(pmMenuLeft);
    }

    @Override
    public void setToolbarTitle(int resTitle, String title) {
        if (resTitle != -1) {
            pmToolbarTitle.setText(resTitle);
        } else {
            pmToolbarTitle.setText(title);
        }
    }

    @Override
    public void setMenuButtonEvent(final View.OnClickListener listener) {
        if (listener != null) {
            pmMenuLeft.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    listener.onClick(view);
                }
            });
        } else {
            pmMenuLeft.setOnClickListener(null);
        }
    }

    @Override
    public void setMenuLeftIcon(int res) {
        if (res == -1) {
            pmMenuLeft.setImageResource(android.R.color.transparent);
        } else {
            pmMenuLeft.setImageResource(res);
        }
    }

    int currentTheme = ToolbarFragment.THEME_DARK;
    @Override
    public void setToolbarTheme(int theme) {
        if(currentTheme==theme)return;
        switch (theme){
            case ToolbarFragment.THEME_LIGHT :
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.text_color_dark));
                llMainView.setBackgroundResource(R.color.white);
                break;
            default:
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.text_color));
                llMainView.setBackgroundResource(R.drawable.pm_bkg);
        }
        currentTheme=theme;
    }

    @Override
    public void setTopFragment(ToolbarFragment topFragment) {
        this.topFragment = topFragment;
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
    public void onScannedIdDataAccepted() {

    }
}
