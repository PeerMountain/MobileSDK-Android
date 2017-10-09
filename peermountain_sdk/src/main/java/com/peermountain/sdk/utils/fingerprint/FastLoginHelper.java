package com.peermountain.sdk.utils.fingerprint;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.SystemHelper;
import com.peermountain.sdk.utils.ripple.RippleUtils;


/**
 * Created by Galeen on 24.6.2017 г..
 */

public class FastLoginHelper {
    private static final int SET_PASSWORD_DEVICE = 516;
    private FingerprintHandler fingerprintHandler;
    private String key;
    private Activity activity;
    private boolean registerFingerprint;
    private Dialog dialogFastLogin;
    private TextView tvFingerBtn, tvTitleFastDialog;
    private TextView tvOr;
    private TextView tvNoFastLoginBtn;
    private TextView tvCodeBtn;
    private EditText etFastCode;
    private CheckBox cbDontAsk;
    private FingerprintHandler.FingerprintEvents callback;

    public FastLoginHelper(Activity activity, String key, boolean registerFingerprint) {
        this.key = key;
        this.activity = activity;
        this.registerFingerprint = registerFingerprint;
    }

    public void checkFastLogin() {
        checkFastLogin(null);
    }

    public void checkFastLogin(FingerprintHandler.FingerprintEvents callback) {
        checkFastLogin(callback, false, true, true);
    }

    public void checkFastLogin(FingerprintHandler.FingerprintEvents callback, boolean disable, boolean
            withFingerprint, boolean withCode) {
        this.callback = callback;
        this.disable = disable;
        if (registerFingerprint) {
//            if (!PersistenceManager.hasFastLogin()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED) {
                    showFastLoginDialog(false, withCode);
                } else {
                    if (disable) {
                        showFastLoginDialog(withFingerprint, withCode);
                    } else
                        showFastLoginDialog(((FingerprintManager) activity.getSystemService(Activity.FINGERPRINT_SERVICE))
                                .isHardwareDetected(), withCode);
                }

            } else {
                showFastLoginDialog(false, withCode);
            }
//            }
        } else {//just listen
            init();
        }

    }

    public boolean autoPopUp = true;

    private void showFastLoginDialog(boolean withFingerprint, boolean withCode) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        View view = getDialogViews();
        if (disable) {//set text for disable
            tvTitleFastDialog.setText(R.string.dialog_confirm_user_title);
        }
        if (!registerFingerprint || disable || !autoPopUp) {
            cbDontAsk.setVisibility(View.GONE);
        }
        setCodeBtn(withCode);
        setFingerprintBtn(withFingerprint);
        setRefuseBtn();
        if (fromProfile)//this option is just to set buttons
            fromProfile = false;
        if (!autoPopUp)
            autoPopUp = true;
        dialog.setView(view);
        dialogFastLogin = dialog.show();
    }

    @NonNull
    private View getDialogViews() {
        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.fast_login_dialog, null);
        tvTitleFastDialog = (TextView) view.findViewById(R.id.tvTitleFastDialog);
        tvFingerBtn = (TextView) view.findViewById(R.id.tvFingerBtn);
        tvOr = (TextView) view.findViewById(R.id.tvOr);
        tvNoFastLoginBtn = (TextView) view.findViewById(R.id.tvNoFastLoginBtn);
        tvCodeBtn = (TextView) view.findViewById(R.id.tvCodeBtn);
        etFastCode = (EditText) view.findViewById(R.id.etFastCode);
        cbDontAsk = (CheckBox) view.findViewById(R.id.cbDontAsk);
        return view;
    }

    private void setRefuseBtn() {
        tvNoFastLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDontAsk.getVisibility() == View.VISIBLE) {
                    refuseFastLogin(!cbDontAsk.isChecked());
                }
//                else
//                    refuseFastLogin(disableFlag);

                SystemHelper.hideKeyboard(activity, etFastCode);
                dialogFastLogin.dismiss();
            }
        });
        RippleUtils.setRippleEffect(tvNoFastLoginBtn);
    }

    /**
     * This flag is set in profile fragment to notify is just authentication
     */
    public boolean fromProfile = false;

    private void setFingerprintBtn(boolean withFingerprint) {
        if (withFingerprint) {
            if (fromProfile) {
                init();
            } else {
                tvFingerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        init();
                    }
                });
                RippleUtils.setRippleEffect(tvFingerBtn);
            }
        } else {
            tvFingerBtn.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
        }
    }

    private void setCodeBtn(boolean withCode) {
        if (withCode) {
//            if (fromProfile) {
//                tvCodeBtn.setText(R.string.btn_authenticate_code);
//            }
//            tvCodeBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (etFastCode.getText().length() < 4) {
//                        etFastCode.setError(activity.getString(R.string.err_no_such_code));
//                    } else {
//                        if (disable) {//confirm is the user
//                            String[] credentials = PersistenceManager.getCredentialsForCode(etFastCode.getText().toString());
//                            if (credentials[0] == null) {//no credentials for this code
//                                etFastCode.setError(activity.getString(R.string.err_no_saved_code));
//                            } else {// good
//                                PersistenceManager.removeUserForFastCode(etFastCode.getText().toString());
//                                if (callback != null)
//                                    callback.onSuccess();
//                                dialogFastLogin.dismiss();
//                            }
//                        } else {//this is register so save the user
//                            PersistenceManager.saveUserForFastCode(etFastCode.getText().toString());
//                            FragmentUtils.hideKeyboard(activity, etFastCode);
//                            dialogFastLogin.dismiss();
//                            showFastLoginInfoDialog(false);
//                            if (callback != null)
//                                callback.onSuccess();
//                        }
//                    }
//                }
//            });
//            RippleUtils.setRippleEffect(tvCodeBtn);
        } else {
            tvCodeBtn.setVisibility(View.GONE);
            etFastCode.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
        }
    }

    private boolean disable = false;

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintHandler == null)
                fingerprintHandler = new FingerprintHandler(activity, key, new FingerprintHandler.FingerprintEvents() {
                    @Override
                    public void onSuccess() {
                        if (dialogFastLogin != null)
                            dialogFastLogin.dismiss();
                        if (registerFingerprint && !disable) {
                            showFastLoginInfoDialog(true);
//                            PersistenceManager.saveFingerprintUser();
                        }
                        if (callback != null)
                            callback.onSuccess();

                    }
                });
            if (!fingerprintHandler.isInitialized()) {
                String result = fingerprintHandler.init(key);
                handleFingerprintResult(result, tvFingerBtn);
            } else
                startListenForFingerprint(tvFingerBtn);
        }
    }

    private void refuseFastLogin(boolean disable) {
        if (registerFingerprint && !disable) {//don't ask him again
//            PersistenceManager.saveDontAksUser(true);
        }
//        else{}// this is from login so nothing


    }

    private void showFastLoginInfoDialog(boolean withFingerprint) {
//        DialogUtils.showSimpleDialog(activity,R.string.dialog_fingerprint_info_title,
//                withFingerprint ? R.string.dialog_fingerprint_info : R.string.dialog_fastlogin_info,null);
    }

    private void handleFingerprintResult(String result, TextView tv) {
        switch (result) {
            case FingerprintHandler.SUCCESS:
                startListenForFingerprint(tv);
                break;
            case FingerprintHandler.ERR_NO_PERMISSION:
                DialogUtils.showError(activity, R.string.err_no_fingerprint_permission);
                if (dialogFastLogin != null)
                    dialogFastLogin.dismiss();
                break;
            case FingerprintHandler.ERR_NO_SECURITY:
                handleNoSecurityDevice(false);
                break;
            case FingerprintHandler.ERR_NO_FINGERPRINT:
                handleNoSecurityDevice(true);
                break;

            case FingerprintHandler.ERR_NO_HARDWARE:
                DialogUtils.showError(activity, result);
                break;//don't offer
            case FingerprintHandler.ERR_KEY:
                DialogUtils.showError(activity, result);
                break;//don't offer
            case FingerprintHandler.ERR_LOW_API:
                DialogUtils.showError(activity, result);
                break;//don't offer
            default:
                DialogUtils.showError(activity, result);
        }
    }

    private void startListenForFingerprint(TextView tv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintHandler != null)
                fingerprintHandler.startAuth();
            if (tv != null) {
                tv.setTextColor(ContextCompat.getColor(activity, R.color.green));
                tv.setText(R.string.position_finger_for_scan);
            }
        }
    }

    private void handleNoSecurityDevice(boolean isFingerprint) {
        DialogUtils.showChoiceDialog(activity, isFingerprint ? R.string.err_no_saved_fingerprint :
                        R.string.err_no_keyguard, -1,

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                        activity.startActivityForResult(intent, SET_PASSWORD_DEVICE);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refuseFastLogin(false);
                        if (dialogFastLogin != null)
                            dialogFastLogin.dismiss();
                    }
                },
                R.string.btn_add_pass,R.string.btn_dont_add_pass);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return fingerprintHandler != null && fingerprintHandler.onActivityResult(requestCode, resultCode, data);
        }
        return false;
    }

    public boolean isRegisterFingerprint() {
        return registerFingerprint;
    }

    public void setRegisterFingerprint(boolean registerFingerprint) {
        this.registerFingerprint = registerFingerprint;
    }

    public FingerprintHandler.FingerprintEvents getCallback() {
        return callback;
    }

    public void setCallback(FingerprintHandler.FingerprintEvents callback) {
        this.callback = callback;
    }
}
