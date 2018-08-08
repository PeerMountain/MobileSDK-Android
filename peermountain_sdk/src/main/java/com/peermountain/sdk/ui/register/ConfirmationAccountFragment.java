package com.peermountain.sdk.ui.register;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peermountain.sdk.R;
import com.peermountain.pm_livecycle.ToolbarFragment;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;


public class ConfirmationAccountFragment extends ToolbarFragment {

    private OnFragmentInteractionListener mListener;

    public ConfirmationAccountFragment() {
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
        return inflater.inflate(R.layout.fragment_confirmation_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTheme(ToolbarFragment.THEME_DARK);
        hideToolbar();
        CountDownTimer timer = new CountDownTimer(PeerMountainSdkConstants.CONFIRMATION_TIMER_PERIOD,PeerMountainSdkConstants.CONFIRMATION_TIMER_PERIOD) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if(mListener!=null) mListener.onAccountCreated();
            }
        };
        timer.start();
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onAccountCreated();
    }
}
