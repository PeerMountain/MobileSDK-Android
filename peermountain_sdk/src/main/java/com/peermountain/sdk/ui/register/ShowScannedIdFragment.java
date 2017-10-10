package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentAbstract;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentChip;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentIdentity;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentValidityResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkResult;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;


public class ShowScannedIdFragment extends ToolbarFragment {
    private static final int REQUEST_SCAN_ID = 796;
    private static final String ARG_PARAM1 = "param1";

    private OnFragmentInteractionListener mListener;
    private TextView mTvPmError;
    private ImageView mIvPmFullImage;
    private ImageView mIvPmFullImageBack;
    private TextView mTvPmNumber;
    private TextView mTvPmFirstName;
    private TextView mTvPmLastName;
    private TextView mTvPmCountry;
    private TextView mTvPmExpiration;
    private TextView mTvPmIssued;
    private TextView mTvPmValid;
    private TextView mTvPmDob;
    private ImageView mIvPmFaceImage;
    private ImageView mBtnPmScanIdReject;
    private ImageView mBtnPmScanIdAccept;

    public ShowScannedIdFragment() {
        // Required empty public constructor
    }


    public static ShowScannedIdFragment newInstance(Intent scannedData) {
        ShowScannedIdFragment fragment = new ShowScannedIdFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, scannedData);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    Intent scannedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scannedData = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_scanned_id, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListeners();
        getScannedData( );
        setToolbar(R.drawable.pm_ic_logo,R.string.pm_register_title,null);
        setTheme(ToolbarFragment.THEME_LIGHT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCAN_ID:
//                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (resultCode == Activity.RESULT_OK) {
                    scannedData = data;
                    getScannedData();
                } else {
                    onRejectClickListener.resetConsumed();
                    Toast.makeText(getActivity(), R.string.pm_err_msg_scan_data, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView(View view) {
        mTvPmError = (TextView) view.findViewById(R.id.tvPmError);
        mIvPmFullImage = (ImageView) view.findViewById(R.id.ivPmFullImage);
        mIvPmFullImageBack = (ImageView) view.findViewById(R.id.ivPmFullImageBack);
        mTvPmNumber = (TextView) view.findViewById(R.id.tvPmNumber);
        mTvPmFirstName = (TextView) view.findViewById(R.id.tvPmFirstName);
        mTvPmLastName = (TextView) view.findViewById(R.id.tvPmLastName);
        mTvPmCountry = (TextView) view.findViewById(R.id.tvPmCountry);
        mTvPmExpiration = (TextView) view.findViewById(R.id.tvPmExpiration);
        mTvPmIssued = (TextView) view.findViewById(R.id.tvPmIssued);
        mTvPmValid = (TextView) view.findViewById(R.id.tvPmValid);
        mTvPmDob = (TextView) view.findViewById(R.id.tvPmDob);
        mIvPmFaceImage = (ImageView) view.findViewById(R.id.ivPmFaceImage);
        mBtnPmScanIdReject = (ImageView) view.findViewById(R.id.btnPmScanIdReject);
        mBtnPmScanIdAccept = (ImageView) view.findViewById(R.id.btnPmScanIdAccept);
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        mBtnPmScanIdReject.setOnClickListener(onRejectClickListener);

        mBtnPmScanIdAccept.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if(mListener!=null){
                    mListener.onScannedIdDataAccepted();
                }
            }
        });
        RippleUtils.setRippleEffectCircle(mBtnPmScanIdAccept);
        RippleUtils.setRippleEffectCircle(mBtnPmScanIdReject);
    }

    RippleOnClickListener onRejectClickListener = new RippleOnClickListener(true) {
        @Override
        public void onClickListener(View view) {
            PeerMountainManager.scanId(ShowScannedIdFragment.this, REQUEST_SCAN_ID);
        }
    };

    private void getScannedData() {
        if(scannedData==null) return;
        try {
            final AXTSdkResult result = AXTCaptureInterface.INSTANCE.getResultImageFromCapture(scannedData);
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
        // TODO: 10/10/2017 use placeholders and create a POJO holder
        StringBuilder sb = new StringBuilder();
        setText(mTvPmNumber, "# ", docNumber, "\nno number", sb);
        setText(mTvPmFirstName, "First name : ", firstname, "\nno first name", sb);
        setText(mTvPmLastName, "Last name : ", name, "\nno last name", sb);
        setText(mTvPmCountry, "Country : ", country, "\nno country", sb);
        setText(mTvPmExpiration, "Expiration Date : ", expirationDate, "\nno Expiration Date", sb);
        setText(mTvPmIssued, "Emitted : ", emitDate, "\nno emitDate", sb);
        setText(mTvPmDob, "Dob : ", birthdate, "\nno Dob", sb);
        setText(mTvPmValid, "", validity == AXTDocumentValidityResult.VALID ?
                "Valid" : "Invalid", "", sb);

        setImage(mIvPmFaceImage, imageFace, "\nno face image", sb);
        setImage(mIvPmFullImage, imageCroppped, "\nno mrz image", sb);
        setImage(mIvPmFullImageBack, imageCroppedBack, "\nno verso image", sb);

        if(documentNfc==null){
            sb.append("\nNo NFC data");
        }
        mTvPmError.setText(sb.toString());
    }

    private void setImage(ImageView iv, AXTImageResult imageFace, String error, StringBuilder sb) {
        if (imageFace != null && !TextUtils.isEmpty(imageFace.getImageUri())) {
            LogUtils.d("image",Uri.parse(imageFace.getImageUri()).toString());
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

    public interface OnFragmentInteractionListener {
        void onScannedIdDataAccepted();
    }
}
