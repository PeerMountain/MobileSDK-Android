package com.peermountain.sdk.ui.authorized.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;

public class MyQrCodeFragment extends HomeToolbarFragment {
    private OnFragmentInteractionListener mListener;
    private ImageView ivQrCode;
    private ProgressBar pbPmProgress;
    private FrameLayout mainView;


    public MyQrCodeFragment() {
        // Required empty public constructor
    }

    public static MyQrCodeFragment newInstance() {
        MyQrCodeFragment fragment = new MyQrCodeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_my_qr_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setUpView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView(View view) {
        mainView = (FrameLayout) view;
        ivQrCode = (ImageView) view.findViewById(R.id.ivQrCode);
        pbPmProgress = (ProgressBar) view.findViewById(R.id.pbPmProgress);
    }

    private void setUpView() {
        setToolbarForQR();
        if(qrBitmap!=null){
            showQrCode();
        }else {
            createQrCode();
        }
    }

    private void setToolbarForQR() {
        setToolbar(R.drawable.pm_ic_arrow_back_24dp, R.drawable.pm_ic_camera_24dp, R.string.pm_title_home, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    getActivity().onBackPressed();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQrReader();
            }
        });
    }

    private void createQrCode() {
        new CreateQRCode().execute(PeerMountainManager.getProfile());
    }

    private Bitmap qrBitmap = null;
    private class CreateQRCode extends AsyncTask<Contact, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Contact... contacts) {
            QRCodeWriter writer = new QRCodeWriter();
            String contentContact = new Gson().toJson(contacts[0]);
            int size = getResources().getDimensionPixelSize(R.dimen.pm_qr_icon_size);
            try {
                BitMatrix bitMatrix = writer.encode(contentContact, BarcodeFormat.QR_CODE, size, size);

                Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        if (bitMatrix.get(x, y))
                            bmp.setPixel(x, y, ContextCompat.getColor(getContext(), R.color.pm_qr_code));
                        else
                            bmp.setPixel(x, y, Color.WHITE);
                    }
                }
                return bmp;
            } catch (WriterException e) {
                Log.e("QR ERROR", "" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            qrBitmap = bitmap;
            showQrCode();
        }
    }

    private void showQrCode() {
        pbPmProgress.setVisibility(View.GONE);
        if (qrBitmap != null) {
            ivQrCode.setImageBitmap(qrBitmap);
        } else {
            // TODO: 10/16/17 show error
        }
    }

    private void showQrReader() {
        if(mListener!=null){
            mListener.showQrReader();
        }
    }


    public interface OnFragmentInteractionListener {
        void showQrReader();
    }
}
