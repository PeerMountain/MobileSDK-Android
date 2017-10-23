package com.peermountain.sdk.ui.authorized.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;

public class HomeJobFragment extends HomeToolbarFragment {
    View llHomeJobProgress;
    ImageView ivHomeJob;
    View flBtnAgree;
    int progress = 0;
    private OnFragmentInteractionListener mListener;

    private final int EVENT_PROGRESS_FINISHED = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PROGRESS_FINISHED:
                    mListener.onJobFinished();
                    break;
            }
        }
    };

    public HomeJobFragment() {
    }

    @Override
    public void setHomeToolbar(@StringRes int title){
        setToolbar(R.drawable.pm_ic_logo_white, R.drawable.pm_ic_qrcode, title, homeToolbarEvents!=null?homeToolbarEvents.getOpenMenuListener():null, homeToolbarEvents!=null?homeToolbarEvents.getOpenBarcodeListener():null);
    }
    // TODO: Rename and change types and number of parameters
    public static HomeJobFragment newInstance() {
        HomeJobFragment fragment = new HomeJobFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home_job, container, false);
        return rootView;
    }

    void initViewComponents(){
        switch (progress){
            case 0:
                ivHomeJob.setImageResource(R.drawable.pm_documents_to_sign_1);
                break;
            case 1:
                ivHomeJob.setImageResource(R.drawable.pm_documents_to_sign_2);
                break;
            case 2:
                ivHomeJob.setImageResource(R.drawable.pm_documents_to_sign_3);
                break;
            case 3:
                ivHomeJob.setVisibility(View.GONE);
                flBtnAgree.setVisibility(View.GONE);
                llHomeJobProgress.setVisibility(View.VISIBLE);
                Message msg = handler.obtainMessage(EVENT_PROGRESS_FINISHED);
                handler.sendMessageDelayed(msg, 1500);
                break;
            default:
                //new RuntimeException("Invalid progress");
                break;

        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        setUpView();
        setListeners();
        initViewComponents();
    }

    private void getViews(View view) {
        ivHomeJob = (ImageView) view.findViewById(R.id.ivHomeJob);
        flBtnAgree = view.findViewById(R.id.flBtnAgree);
        llHomeJobProgress = view.findViewById(R.id.llHomeJobProgress);
    }

    private void setUpView() {
        //setHomeToolbar(R.string.pm_title_application);
        llHomeJobProgress.setVisibility(View.GONE);
    }

    private void setListeners() {
        flBtnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress++;
                initViewComponents();
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onJobFinished();
    }
}
