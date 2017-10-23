package com.peermountain.sdk.ui.authorized.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;


public class ShareFragment extends HomeToolbarFragment {

    private OnFragmentInteractionListener mListener;
    private FrameLayout flNB;
    private FrameLayout flQR;

    public ShareFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListeners();

        setToolbar(R.drawable.pm_ic_close_24dp, R.string.pm_share_title, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null) mListener.onShareRefused();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView(View view) {
        flNB = (FrameLayout) view.findViewById(R.id.flNB);
        flQR = (FrameLayout) view.findViewById(R.id.flQR);
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        flQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onRequestShareByQRCode();
            }
        });
        flNB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onRequestShareByNearBy();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onShareRefused();
        void onRequestShareByQRCode();
        void onRequestShareByNearBy();
    }
}
