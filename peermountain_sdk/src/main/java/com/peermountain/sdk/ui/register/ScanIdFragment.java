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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;


public class ScanIdFragment extends ToolbarFragment {
    private static final int REQUEST_SCAN_ID = 786;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView pmIvNext;

    public ScanIdFragment() {
        // Required empty public constructor
    }

    public static ScanIdFragment newInstance(String param1, String param2) {
        ScanIdFragment fragment = new ScanIdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_scan_id, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListeners();
        setToolbar(R.drawable.pm_ic_logo, R.string.pm_register_title, null);
        setTheme(ToolbarFragment.THEME_DARK);

        if(mListener != null) {
            if (mListener.isScanSDKReady()) {
                onScanSDKEnabledLocal(true);
            } else {
                onScanSDKEnabledLocal(false);
                mListener.initScanIdSDK();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCAN_ID:
//                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (resultCode == Activity.RESULT_OK) {
                    if (mListener != null) mListener.onIdScanned(data);
                } else {
                    onNextClickListener.resetConsumed();
                    Toast.makeText(getActivity(), R.string.pm_err_msg_scan_data, Toast.LENGTH_SHORT).show();
                }
                pbPmProgress.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * This method will be called from the parent activity when the SDK is done
     * @param enabled true = ready , false = error
     */
    public void onScanSDKEnabled(boolean enabled) {
        if(!enabled){
            LogUtils.e("initScanSDK", "error");
            Toast.makeText(getActivity(), R.string.pm_err_msg_init_scan_id, Toast.LENGTH_SHORT).show();
        }
        onScanSDKEnabledLocal(enabled);
    }

    private void onScanSDKEnabledLocal(boolean enabled) {
        pbPmProgress.setVisibility(!enabled?View.VISIBLE:View.GONE);
        pmIvNext.setEnabled(enabled);
        rippleIvNext.setEnabled(enabled);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    MaterialRippleLayout rippleIvNext;
    ProgressBar pbPmProgress;

    private void initView(View view) {
        pbPmProgress = view.findViewById(R.id.pbPmProgress);
        pmIvNext = (ImageView) view.findViewById(R.id.pmIvNext);
        rippleIvNext = RippleUtils.setRippleEffectSquare(pmIvNext);
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        pmIvNext.setOnClickListener(onNextClickListener);
    }

    RippleOnClickListener onNextClickListener = new RippleOnClickListener(true) {
        @Override
        public void onClickListener(View view) {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            pbPmProgress.setVisibility(View.VISIBLE);//useless
            // TODO: 10/11/2017 show loading text the SDK is slow
            PeerMountainManager.scanId(ScanIdFragment.this, REQUEST_SCAN_ID);
        }
    };

    public interface OnFragmentInteractionListener {
        boolean isScanSDKReady();

        void initScanIdSDK();

        void onIdScanned(Intent scannedData);
    }
}
