package com.peermountain.sdk.utils.fingerprint;

/**
 * Created by Galeen on 23.6.2017 г..
 */

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.peermountain.sdk.utils.DialogUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;



@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    public static final String SUCCESS = "SUCCESS";
    public static final String ERR_KEY = "ERR_KEY";
    public static final String ERR_LOW_API = "SDK < 16";
    public static final String ERR_NO_HARDWARE = "Your device doesn't support fingerprint authentication";
    public static final String ERR_NO_PERMISSION = "Please enable the fingerprint permission";
    public static final String ERR_NO_FINGERPRINT = "No fingerprint configured. Please register at least one " +
            "fingerprint in your device's Settings";
    public static final String ERR_NO_SECURITY = "Please enable lock screen security in your device's Settings";

    private static final int SET_PASSWORD_DEVICE = 516;
    private String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private CancellationSignal cancellationSignal;
    private Activity activity;
    private boolean initialized;
    private FingerprintEvents callback;


    public FingerprintHandler(Activity activity,String key,  FingerprintEvents callback) {
        this.KEY_NAME = key;
        this.activity = activity;
        this.callback = callback;
    }

    public String init(String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            KEY_NAME = key;

            keyguardManager =
                    (KeyguardManager) activity.getSystemService(Activity.KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) activity.getSystemService(Activity.FINGERPRINT_SERVICE);

            if (fingerprintManager!=null && !fingerprintManager.isHardwareDetected()) {
                return ERR_NO_HARDWARE;
            }


            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return ERR_NO_PERMISSION;

            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                return ERR_NO_FINGERPRINT;

            }

            if (!keyguardManager.isKeyguardSecure()) {
                return ERR_NO_SECURITY;
            } else {
                try {

                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    initialized = true;
                    return SUCCESS;
                }

                return ERR_KEY;
            }
        } else
            return ERR_LOW_API;
    }


    public void startAuth() {
        startAuth(fingerprintManager, cryptoObject);
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        DialogUtils.showError(activity, "Authentication error\n" + errString);
//        Toast.makeText(activity,
//                "Authentication error\n" + errString,
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        DialogUtils.showError(activity, "Authentication failed");
//        Toast.makeText(activity,
//                "Authentication failed",
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(activity,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        if (callback != null)
            callback.onSuccess();

//        Toast.makeText(activity,
//                "Success!",
//                Toast.LENGTH_LONG).show();
    }

    public interface FingerprintEvents {
        void onSuccess();
    }


    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }



    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }


//    public void checkFastLogin() {
//        checkFastLogin(null);
//    }
//
//    public void checkFastLogin(FingerprintEvents callback) {
//        this.callback = callback;
//        if (registerFingerprint) {
//            if (PersistenceManager.getLogin().getUserId() != PersistenceManager.getFingerprintUserId()
//                    && !PersistenceManager.isCurrentInFastUserIds()) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                        showFastLoginDialog(false);
//                    } else
//                        showFastLoginDialog(((FingerprintManager) activity.getSystemService(Activity.FINGERPRINT_SERVICE))
//                                .isHardwareDetected());
//
//                } else {
//                    showFastLoginDialog(false);
//                }
//            }
//        } else {//just listen
//            init();
//        }
//
//    }
//
//    private void showFastLoginDialog(boolean withFingerprint) {
//        dialogFastLogin = new Dialog(activity);
//        dialogFastLogin.setCancelable(false);
//        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = vi.inflate(R.layout.fast_login_dialog, null);
//        tvFingerBtn = (TextView) view.findViewById(R.id.tvFingerBtn);
//        TextView tvOr = (TextView) view.findViewById(R.id.tvOr);
//        TextView tvNoFastLoginBtn = (TextView) view.findViewById(R.id.tvNoFastLoginBtn);
//        TextView tvCodeBtn = (TextView) view.findViewById(R.id.tvCodeBtn);
//        final EditText etFastCode = (EditText) view.findViewById(R.id.etFastCode);
//        if (withFingerprint) {
//            tvFingerBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    init();
//                }
//            });
//            RippleUtils.setRippleEffect(tvFingerBtn);
//        } else {
//            tvFingerBtn.setVisibility(View.GONE);
//            tvOr.setVisibility(View.GONE);
//        }
//
//        tvCodeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (etFastCode.getText().length() < 4) {
////                    DialogUtils.showError(MainActivity.this, R.string.err_save_code);
//                    etFastCode.setError(activity.getString(R.string.err_save_code));
//                } else {
//                    PersistenceManager.saveUserForFastCode(etFastCode.getText().toString());
//                    FragmentUtils.hideKeyboard(activity, etFastCode);
//                    dialogFastLogin.dismiss();
//                    showFastloginInfoDialog(false);
//                }
//            }
//        });
//
//        tvNoFastLoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refuseFastLogin();
//                FragmentUtils.hideKeyboard(activity, etFastCode);
//                dialogFastLogin.dismiss();
//            }
//        });
//        RippleUtils.setRippleEffect(tvCodeBtn);
//        RippleUtils.setRippleEffect(tvNoFastLoginBtn);
//        dialogFastLogin.setContentView(view);
//        dialogFastLogin.show();
//    }
//
//    private void init() {
//        if (!initialized) {
//            String result = init(KEY_NAME);
//            handleFingerprintResult(result, tvFingerBtn);
//        }
//    }
//
//    private void refuseFastLogin() {
//        if (registerFingerprint) {//don't ask him again
//            PersistenceManager.saveDontAksUser(true);
//        }
////        else{}// this is from login so nothing
//
//
//    }
//
//    private void showFastloginInfoDialog(boolean withFingerprint) {
//        Dialog dialog = new Dialog(activity);
//        dialog.setTitle(withFingerprint ? R.string.dialog_fingerprint_info : R.string.dialog_fastlogin_info);
//        dialog.show();
//    }
//
//    private void handleFingerprintResult(String result, TextView tv) {
//        switch (result) {
//            case FingerprintHandler.SUCCESS:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    startAuth();
//                    if(tv!=null) {
//                        tv.setTextColor(ContextCompat.getColor(activity, R.color.green));
//                        tv.setText(R.string.position_finger_for_scan);
//                    }
//                }
//                break;
//            case FingerprintHandler.ERR_NO_PERMISSION:
//                DialogUtils.showError(activity, R.string.err_no_fingerprint_permission);
//                if (dialogFastLogin != null)
//                    dialogFastLogin.dismiss();
//                break;
//            case FingerprintHandler.ERR_NO_SECURITY:
//                handleNoSecurityDevice(false);
//                break;
//            case FingerprintHandler.ERR_NO_FINGERPRINT:
//                handleNoSecurityDevice(true);
//                break;
//
//            case FingerprintHandler.ERR_NO_HARDWARE:
//                DialogUtils.showError(activity, result);
//                break;//don't offer
//            case FingerprintHandler.ERR_KEY:
//                DialogUtils.showError(activity, result);
//                break;//don't offer
//            case FingerprintHandler.ERR_LOW_API:
//                DialogUtils.showError(activity, result);
//                break;//don't offer
//            default:
//                DialogUtils.showError(activity, result);
//        }
//    }
//
//    private void handleNoSecurityDevice(boolean isFingerprint) {
//        DialogUtils.showChoiceDialog(activity, isFingerprint ? R.string.err_no_saved_fingerprint :
//                        R.string.err_no_keyguard, -1,
//                R.string.btn_add_pass,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//                        activity.startActivityForResult(intent, SET_PASSWORD_DEVICE);
//                    }
//                },
//                R.string.btn_dont_add_pass,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        refuseFastLogin();
//                        if (dialogFastLogin != null)
//                            dialogFastLogin.dismiss();
//                    }
//                });
//    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean res = false;
        switch (requestCode) {
            case SET_PASSWORD_DEVICE:
                res = true;
                init(KEY_NAME);
                break;
        }
        return res;
    }
}