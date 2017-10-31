package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.documents.PmDocumentsHelper;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.DocumentUtils;
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
        return inflater.inflate(R.layout.pm_fragment_show_scanned_id, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//        setListeners();
//        getScannedData(scannedData);
//        setToolbar(R.drawable.pm_ic_logo,R.string.pm_register_title,null);
//        setTheme(ToolbarFragment.THEME_LIGHT);
        if(mListener!=null){
            document = PmDocumentsHelper.getScannedData(scannedData);
            mListener.onScannedIdDataAccepted(document);
        }
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
                    getScannedData(scannedData);
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
                    mListener.onScannedIdDataAccepted(document);
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

    DocumentID document;
    private void getScannedData(Intent scannedData) {
        document = DocumentUtils.getScannedData(scannedData);
        setDataInView(document);
    }

    private void setDataInView(DocumentID document) {
        if(document==null) return;
        StringBuilder sb = new StringBuilder();

        DocumentUtils.setImage(mIvPmFullImage, document.getImageCropped(), "\nno ID image", sb);
        DocumentUtils.setImage(mIvPmFullImageBack, document.getImageCroppedBack(), "\nno ID verso image", sb);

        // TODO: 10/10/2017 comment bellow if you just want to show ID images
        showExtraInfo(document, sb);
//        mTvPmError.setVisibility(View.GONE);
    }

    private void showExtraInfo(DocumentID document, StringBuilder sb) {
        DocumentUtils.setImage(mIvPmFaceImage, document.getImageFace(), "\nno face image", sb);
        DocumentUtils.setText(mTvPmNumber, "# ", document.getDocNumber(), "\nno number", sb);
        DocumentUtils.setText(mTvPmFirstName, "First name : ", document.getFirstName(), "\nno first name", sb);
        DocumentUtils.setText(mTvPmLastName, "Last name : ", document.getLastName(), "\nno last name", sb);
        DocumentUtils.setText(mTvPmCountry, "Country : ", document.getCountry(), "\nno country", sb);
        DocumentUtils.setText(mTvPmExpiration, "Expiration Date : ", document.getExpirationDate(), "\nno Expiration Date", sb);
        DocumentUtils.setText(mTvPmIssued, "Emitted : ", document.getEmitDate(), "\nno emitDate", sb);
        DocumentUtils.setText(mTvPmDob, "Dob : ", document.getBirthday(), "\nno Dob", sb);
        DocumentUtils.setText(mTvPmValid, "", document.isValid() ?"Valid" : "Invalid", "", sb);

//        if(document.getScannedResult().getMapDocument().get(AXTSdkResult.RFID_DOCUMENT)==null){
//            sb.append("\nNo NFC data");
//        }
        mTvPmError.setText(sb.toString());
    }

    public interface OnFragmentInteractionListener {
        void onScannedIdDataAccepted(DocumentID scannedDocument);
    }
}
