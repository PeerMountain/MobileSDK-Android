package com.peermountain.sdk.ui.register;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.peermountain.sdk.views.PeerMountainTextView;

public class RegisterKeywordsFragment extends ToolbarFragments {

    private OnFragmentInteractionListener mListener;
    private PeerMountainTextView pmTvMessageKeywords;
    private EditText pmEtKeywords;
    private ImageView pmIvValid;

    public RegisterKeywordsFragment() {
        // Required empty public constructor
    }

    public static RegisterKeywordsFragment newInstance() {
        RegisterKeywordsFragment fragment = new RegisterKeywordsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_register_keywords, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setToolbar();
        setListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setToolbar() {
        if (mToolbarListener == null) return;
        mToolbarListener.setMenuLeftIcon(-1);
        mToolbarListener.setToolbarTitle(R.string.pm_register_title, null);
        mToolbarListener.setMenuButtonEvent(null);
    }

    private void initView(View view) {
        pmTvMessageKeywords = (PeerMountainTextView) view.findViewById(R.id.pmTvMessageKeywords);
        pmEtKeywords = (EditText) view.findViewById(R.id.pmEtKeywords);
        pmIvValid = (ImageView) view.findViewById(R.id.pmIvValid);
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        pmIvValid.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                // TODO: 10/9/17 check words, save them and redirect
//                if (mListener != null)
//                    mListener.;
            }
        });
        RippleUtils.setRippleEffectSquare(pmIvValid);
    }

    public interface OnFragmentInteractionListener {
    }
}
