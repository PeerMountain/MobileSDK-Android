package com.peermountain.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentAbstract;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentChip;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentIdentity;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentValidityResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkResult;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ScanIdActivity extends AppCompatActivity {
    private static final int REQUEST_SCAN_ID = 786;
    private TextView mTvPmNumber;
    private TextView mTvPmFirstName;
    private TextView mTvPmLastName;
    private TextView mTvPmCountry;
    private TextView mTvPmExpiration;
    private TextView mTvPmValid;
    private TextView mTvPmDob;
    private ImageView mIvPmFaceImage;
    private ImageView mIvPmFullImage;
    private ImageView mIvPmFullImageBack;
    private Button mBtnPmScanId;
    private TextView mTvPmError;
    private ProgressBar mPbPmProgress;
    private TextView mTvPmIssued;

    public static void show(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, ScanIdActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_scan_id);
        initView();
        setListeners();
        mBtnPmScanId.setEnabled(false);
        intScanSDK();
        setResult(RESULT_CANCELED);
    }

    private void intScanSDK() {
        mPbPmProgress.setVisibility(View.VISIBLE);
        PeerMountainManager.initScanSDK(this,
                new AXTCaptureInterfaceCallback() {
                    @Override
                    public void onInitSuccess() {
                        mBtnPmScanId.setEnabled(true);
                        mPbPmProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onInitError() {
                        mPbPmProgress.setVisibility(View.GONE);
                        LogUtils.e("initScanSDK", "error");
                        Toast.makeText(ScanIdActivity.this, R.string.pm_err_msg_init_scan_id, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCAN_ID:
//                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (resultCode == RESULT_OK) {
                    getScannedData(data);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final boolean permissionsAllowed =
                AXTCaptureInterface.INSTANCE.verifyPermissions(requestCode, permissions, grantResults);
        if (permissionsAllowed) {
            intScanSDK();
        } else {
            mTvPmFirstName.setText(R.string.pm_err_msg_permission_scan_id);
        }
    }


    private void getScannedData(Intent data) {
        try {
            final AXTSdkResult result = AXTCaptureInterface.INSTANCE.getResultImageFromCapture(data);
            final AXTImageResult imageSource = result.getMapImageSource().get(AXTSdkResult.IMAGES_RECTO);
            final AXTImageResult imageSourceBack = result.getMapImageSource().get(AXTSdkResult.IMAGES_VERSO);
            final AXTImageResult imageCroppped = result.getMapImageCropped().get(AXTSdkResult.IMAGES_RECTO);
            final AXTImageResult imageCroppedBack = result.getMapImageCropped().get(AXTSdkResult.IMAGES_VERSO);
            final AXTImageResult imageFace = result.getMapImageFace().get(AXTSdkResult.FACE_CROPPED);
            AXTDocumentIdentity document = (AXTDocumentIdentity)
                    result.getMapDocument().get(AXTSdkResult.IDENTITY_DOCUMENT);
            // Récupération des champs d'un document d'identité
            final String name = document.getField(AXTDocumentIdentity.AxtField.LAST_NAMES);
            final String firstname = document.getField(AXTDocumentIdentity.AxtField.FIRST_NAMES);
            final String gender = document.getField(AXTDocumentIdentity.AxtField.GENDER);
            final String birthdate = document.getField(AXTDocumentIdentity.AxtField.BIRTH_DATE);

            final String docNumber = document.getField(AXTDocumentIdentity.AxtField.DOCUMENT_NUMBER);
            final String country = document.getField(AXTDocumentIdentity.AxtField.EMIT_COUNTRY);
            String emitDate = document.getField(AXTDocumentIdentity.AxtField.EMIT_DATE);
            final String mrzID = document.getField(AXTDocumentAbstract.AxtField.CODELINE);
            final AXTDocumentValidityResult validity = document.getDocumentValidity();
            AXTDocumentChip documentNfc = (AXTDocumentChip)
                    result.getMapDocument().get(AXTSdkResult.RFID_DOCUMENT);
            String expirationDate = null;
            if (documentNfc != null) {
                expirationDate =documentNfc.getField(AXTDocumentChip.AxtField.EXPIRATION_DATE);
            }
            setDataInView(imageCroppped, imageFace, name, firstname, birthdate, country, validity, docNumber, emitDate, imageCroppedBack, expirationDate,documentNfc);
        } catch (CaptureApiException e) {
            e.printStackTrace();
        }
    }

    private void setDataInView(AXTImageResult imageCroppped, AXTImageResult imageFace, String name, String firstname, String birthdate, String country, AXTDocumentValidityResult validity, String docNumber, String emitDate,
                               AXTImageResult imageCroppedBack,String expirationDate, AXTDocumentChip documentNfc) {
        StringBuilder sb = new StringBuilder();
        setText(mTvPmNumber, "# ", docNumber, "\nno number", sb);
        setText(mTvPmFirstName, "first name : ", firstname, "\nno first name", sb);
        setText(mTvPmLastName, "last name : ", name, "\nno last name", sb);
        setText(mTvPmCountry, "Country : ", country, "\nno country", sb);
        setText(mTvPmExpiration, "Expiration Date : ", expirationDate, "\nno Expiration Date", sb);
        setText(mTvPmIssued, "Emitted : ", emitDate, "\nno emitDate", sb);
        setText(mTvPmDob, "Dob : ", birthdate, "\nno Dob", sb);
        setText(mTvPmValid, "", validity == AXTDocumentValidityResult.VALID ?
                "Valid" : "Invalid", "", sb);

        setImage(mIvPmFaceImage, imageFace, "\nno face btn", sb);
        setImage(mIvPmFullImage, imageCroppped, "\nno mrz btn", sb);
        setImage(mIvPmFullImageBack, imageCroppedBack, "\nno verso btn", sb);

        if(documentNfc==null){
            sb.append("\nNo NFC data");
        }
        mTvPmError.setText(sb.toString());
        if (!TextUtils.isEmpty(docNumber) && !docNumber.equalsIgnoreCase("null")) {
            Intent data = new Intent();
            data.putExtra(PeerMountainSdkConstants.EXTRA_ID_NUMBER, docNumber);
            setResult(RESULT_OK, data);
        }
    }

    private void setImage(ImageView iv, AXTImageResult imageFace, String error, StringBuilder sb) {
        if (imageFace != null && !TextUtils.isEmpty(imageFace.getImageUri())) {
            iv.setImageURI(Uri.parse(imageFace.getImageUri()));
            iv.setVisibility(View.VISIBLE);
        } else {
            sb.append(error);
            iv.setVisibility(View.GONE);
        }
    }

    private void setText(TextView tv, String prefix, String value, String error, StringBuilder sb) {
        if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null")) {
            tv.setText(prefix + value);
            tv.setVisibility(View.VISIBLE);
        } else {
            sb.append(error);
            tv.setVisibility(View.GONE);
        }
    }

    /**
     * type ocl to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        mBtnPmScanId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnPmScanId.setText(R.string.pm_btn_re_scan_id);
                openScanActivity();
            }
        });
    }

    private void openScanActivity() {
        mTvPmFirstName.setText("");

//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mIvPmFaceImage.setImageURI(null);
        mIvPmFullImage.setImageURI(null);
        mIvPmFullImageBack.setImageURI(null);
        PeerMountainManager.scanId(ScanIdActivity.this, REQUEST_SCAN_ID);
    }

    private void initView() {
        mTvPmNumber = (TextView) findViewById(R.id.tvPmNumber);
        mTvPmFirstName = (TextView) findViewById(R.id.tvPmFirstName);
        mTvPmLastName = (TextView) findViewById(R.id.tvPmLastName);
        mTvPmCountry = (TextView) findViewById(R.id.tvPmCountry);
        mTvPmExpiration = (TextView) findViewById(R.id.tvPmExpiration);
        mTvPmValid = (TextView) findViewById(R.id.tvPmValid);
        mTvPmDob = (TextView) findViewById(R.id.tvPmDob);
        mIvPmFaceImage = (ImageView) findViewById(R.id.ivPmFaceImage);
        mIvPmFullImage = (ImageView) findViewById(R.id.ivPmFullImage);
        mIvPmFullImageBack = (ImageView) findViewById(R.id.ivPmFullImageBack);
        mBtnPmScanId = (Button) findViewById(R.id.btnPmScanId);
        mTvPmError = (TextView) findViewById(R.id.tvPmError);
        mPbPmProgress = (ProgressBar) findViewById(R.id.pbPmProgress);
        mTvPmIssued = (TextView) findViewById(R.id.tvPmIssued);
    }
}
