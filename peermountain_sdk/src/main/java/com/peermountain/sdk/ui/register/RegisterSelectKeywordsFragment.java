package com.peermountain.sdk.ui.register;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.core.model.unguarded.Keyword;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.views.PeerMountainTextView;

import java.util.ArrayList;

public class RegisterSelectKeywordsFragment extends ToolbarFragment {

    public static final String ARG_VALIDATE = "validate";
    private OnFragmentInteractionListener mListener;
    private PeerMountainTextView mTvTitle;
    private PeerMountainTextView mTvMsg;
    private GridLayout mGridKeywords;
    private PeerMountainTextView mTvSkip;
    private ImageView mIvNext;
    private PeerMountainTextView mTvNext;

    private boolean isValidate = false;

    public RegisterSelectKeywordsFragment() {
        // Required empty public constructor
    }

    public static RegisterSelectKeywordsFragment newInstance(boolean validate) {
        RegisterSelectKeywordsFragment fragment = new RegisterSelectKeywordsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_VALIDATE,validate);
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
            isValidate = getArguments().getBoolean(ARG_VALIDATE,false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_register_select_keywords, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PeerMountainManager.saveKeywordsObject(null);//remove current
        initView(view);
        setUpView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUpView() {
        if(isValidate){
            setViewForValidate();
        }else{
            setViewForRegister();
        }
    }

    ArrayList<View> keywordViews = new ArrayList<>();
    public void setViewForRegister() {
        setToolbarForKeywords();
        mTvTitle.setText(R.string.pm_keywords);
        mTvMsg.setText(R.string.pm_keywords_select_message);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvNext.setVisibility(View.VISIBLE);
        mIvNext.setVisibility(View.GONE);
        ArrayList<Keyword> keywords = PeerMountainManager.getRandomKeywords(getActivity()).getKeywords();
        if (keywords != null) {
            int textColor = ContextCompat.getColor(getActivity(),R.color.pm_text_color);
            for (Keyword type : keywords) {
//                PeerMountainTextView view = new PeerMountainTextView(getActivity());
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView view = (TextView) inflater.inflate(R.layout.pm_keyword_view, mGridKeywords,false);
                view.setTag(type);
//                view.setBackgroundResource(R.drawable.pm_keywords_selector);
                view.setText(type.getValue());
                view.setSelected(type.isSelected());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Keyword keyword = (Keyword) view.getTag();
                        keyword.setSelected(!keyword.isSelected());
                        view.setSelected(keyword.isSelected());
                    }
                });
                mGridKeywords.addView(view);
                keywordViews.add(view);
            }
        }
    }

    public void setViewForValidate() {
        setToolbarForKeywords();
        mTvTitle.setText(R.string.pm_show_keywords_title);
        mTvMsg.setText(R.string.pm_saved_keywords_message);
        mTvSkip.setVisibility(View.GONE);
        mTvNext.setVisibility(View.GONE);
        mIvNext.setVisibility(View.VISIBLE);
    }

    private void setToolbarForKeywords() {
        setToolbar(R.drawable.pm_ic_logo, R.string.pm_register_title, null);
        hideToolbar();
    }

    private void initView(View view) {
        mTvTitle = (PeerMountainTextView) view.findViewById(R.id.tvTitle);
        mTvMsg = (PeerMountainTextView) view.findViewById(R.id.tvMsg);
        mGridKeywords = (GridLayout) view.findViewById(R.id.gridKeywords);
        mTvSkip = (PeerMountainTextView) view.findViewById(R.id.tvSkip);
        mIvNext = (ImageView) view.findViewById(R.id.ivNext);
        mTvNext = (PeerMountainTextView) view.findViewById(R.id.tvNext);
    }

    public interface OnFragmentInteractionListener {
        void onKeywordsSaved();
    }
}
