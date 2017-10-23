package com.peermountain.sdk.ui.authorized.contacts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRFragment extends HomeToolbarFragment implements ZXingScannerView.ResultHandler{
    public static final int REQUEST_CODE_CAMERA = 988588;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScanQRFragment() {
        // Required empty public constructor
    }

    public static ScanQRFragment newInstance() {
        ScanQRFragment fragment = new ScanQRFragment();
        Bundle args = new Bundle();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    FrameLayout mainView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return mainView = (FrameLayout) inflater.inflate(R.layout.pm_fragment_scan_qr, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarForQrReader();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mScannerView!=null) {
            startCamera();
        }else{
            showQrReader();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                // Check Permissions Granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addScannerView();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Camera permission is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        final String qrcode =rawResult.getText().toString();
        Log.i("",""+qrcode);
        if (rawResult.getText().length() > 0) {
//            Contact contact = new Gson().fromJson(qrcode,Contact.class);
            Contact contact = new Contact();
            String[] data = qrcode.split("@#@");
            if(data.length>0)
            contact.setNames(data[0].trim());
            if(data.length>1)
            contact.setDob(data[1].trim());
            if(data.length>2)
            contact.setPob(data[2].trim());
            if(data.length>3)
            contact.setPhone(data[3].trim());
            if(data.length>4)
            contact.setMail(data[4].trim());
            if(mListener!=null){
                mListener.onContactScannedFromQR(contact);
            }
        }

    }

    private void setToolbarForQrReader() {
        setToolbar(R.drawable.pm_ic_arrow_back_24dp, -1, R.string.pm_title_home, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        }, null);
    }

    ZXingScannerView mScannerView;

    private void showQrReader() {
        if (hasPermission(getContext(), Manifest.permission.CAMERA)) {
            addScannerView();
        }else {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }
    }

    private void addScannerView() {
        mScannerView = new ZXingScannerView(getContext());
        mainView.addView(mScannerView);
        startCamera();
    }

    private void stopCamera() {
        try{
            if(mScannerView!=null && hasPermission(getContext(), Manifest.permission.CAMERA)){
                mScannerView.stopCamera();           // Stop camera on pause
            }
        }
        catch (Exception e ){
            LogUtils.e("scannerStop",e.getMessage());
        }
    }

    private void startCamera() {
        try{
            if(mScannerView!=null && hasPermission(getContext(), Manifest.permission.CAMERA)){
                mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                mScannerView.startCamera();
            }
        }
        catch (Exception e ){
            LogUtils.e("scannerStart",e.getMessage());
        }
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public interface OnFragmentInteractionListener {
        void onContactScannedFromQR(Contact contact);
    }
}
