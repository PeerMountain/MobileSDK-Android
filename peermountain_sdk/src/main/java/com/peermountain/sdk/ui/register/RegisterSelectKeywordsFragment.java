package com.peermountain.sdk.ui.register;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.peermountain.core.model.unguarded.Keyword;
import com.peermountain.core.model.unguarded.Keywords;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.peermountain.sdk.views.PeerMountainTextView;

import java.util.ArrayList;
import java.util.HashSet;

import static com.peermountain.core.utils.PmCoreConstants.MIN_KEYWORDS_SAVE;

public class RegisterSelectKeywordsFragment extends ToolbarFragment {

    public static final String ARG_VALIDATE = "validate";
    private OnFragmentInteractionListener mListener;
    private PeerMountainTextView mTvTitle;
    private PeerMountainTextView mTvMsg;
    private GridLayout mGridKeywords;
    private View mLlTvWhy;
    private ImageView mIvNext;
    private PeerMountainTextView mTvNext;

    private boolean isReset = false;

    public RegisterSelectKeywordsFragment() {
        // Required empty public constructor
    }

    public static RegisterSelectKeywordsFragment newInstance(boolean validate) {
        RegisterSelectKeywordsFragment fragment = new RegisterSelectKeywordsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_VALIDATE, validate);
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
            isReset = getArguments().getBoolean(ARG_VALIDATE, false);
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
        setListeners();
        setUpView();
    }

    @Override
    public boolean onBackPressed() {
        if(timer != null) return true;//still going on
        if (!isJustShowing) {
            setUpView();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDetach() {
        stopTimer();
        super.onDetach();
        mListener = null;
    }

    private void onKeywordsConfirmed() {
        mTvTitle.setText(R.string.pm_confirmed_keywords_title);
        mTvMsg.setText(R.string.pm_keywords_confirmed_message);
        mTvNext.setVisibility(View.GONE);
        btnRipple.setVisibility(View.GONE);
        mGridKeywords.setVisibility(View.GONE);
        ivCheck.setVisibility(View.VISIBLE);
        mLlTvWhy.setVisibility(View.GONE);
        timer = new CountDownTimer(PeerMountainSdkConstants.CONFIRMATION_TIMER_PERIOD, PeerMountainSdkConstants.CONFIRMATION_TIMER_PERIOD) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                timer = null;
                PeerMountainManager.saveKeywordsObject(getSelectedKeywords());
                if (mListener != null) {
                    mListener.onKeywordsSaved();
                }
            }
        };
        timer.start();
    }

    CountDownTimer timer = null;

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setUpView() {
        ivCheck.setVisibility(View.GONE);
        if (isReset) {
            setViewForReset();
        } else {
            setViewForRegister();
        }
    }

    boolean isJustShowing = true;
    ArrayList<View> keywordViews = new ArrayList<>();

    public void setViewForRegister() {
        setToolbarForKeywords();
        isJustShowing = true;
        mTvTitle.setText(R.string.pm_keywords);
        mTvMsg.setText(R.string.pm_keywords_select_message);
        mTvNext.setVisibility(View.VISIBLE);
        mIvNext.setVisibility(View.GONE);
        btnRipple.setVisibility(View.VISIBLE);
        mGridKeywords.setVisibility(View.VISIBLE);
        mLlTvWhy.setVisibility(View.VISIBLE);
        ivCheck.setVisibility(View.GONE);
        enableButton(true);
//        if(myKeywords!=null && myKeywords.getKeywords()!=null){
//            generateKeywords(myKeywords.getKeywords());
//        }else {
            ArrayList<Keyword> keywords = PeerMountainManager.getRandomKeywords(getActivity()).getKeywords();
            generateKeywords(keywords);
            myKeywords = new Keywords();
            myKeywords.setKeywords(keywords);
            PeerMountainManager.saveKeywordsObject(myKeywords);
//        }
    }

    private void generateKeywords(final ArrayList<Keyword> keywords) {
        if (keywords != null) {
            mGridKeywords.removeAllViews();
            keywordViews.clear();
            int textColor = ContextCompat.getColor(getActivity(), R.color.pm_text_color);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (Keyword keyword : keywords) {
//                PeerMountainTextView view = new PeerMountainTextView(getActivity());
                TextView view = (TextView) inflater.inflate(R.layout.pm_keyword_view, mGridKeywords, false);
                view.setTag(keyword);
//                view.setBackgroundResource(R.drawable.pm_keywords_selector);
                view.setText(keyword.getValue() + (keyword.isSelected() ? "*" : ""));
//                view.setSelected(keyword.isSelected());
                if (!isJustShowing) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Keyword keyword = (Keyword) view.getTag();
//                            keyword.setSelected(!keyword.isSelected());
                            view.setSelected(!view.isSelected());
                            checkKeywordsCount();
                        }
                    });
                }
                mGridKeywords.addView(view);
                keywordViews.add(view);
            }
        }
    }

    Keywords myKeywords = null;

    private void checkKeywordsCount() {
        if (myKeywords == null || myKeywords.getKeywords() == null) return;
        int count = 0;
        for (View keywordView : keywordViews) {
            if (keywordView.isSelected()) {
                Keyword key = (Keyword) keywordView.getTag();
                if (myKeywords.getKeywords().contains(key)) {
                    count++;
                } else {//wrong selected word
                    count = 0;
                    break;
                }
            }
        }
        enableButton(count >= MIN_KEYWORDS_SAVE);
    }

    private Keywords getSelectedKeywords() {
        HashSet<Keyword> kws = new HashSet<>();
        for (View keywordView : keywordViews) {
            Keyword key = (Keyword) keywordView.getTag();
            if (key.isSelected()) {
                kws.add(key);
            }
        }
        return new Keywords(kws);
    }

    public void setViewForValidate() {
        setToolbarForKeywords();
        mTvTitle.setText(R.string.pm_show_keywords_title);
        mTvMsg.setText(R.string.pm_saved_keywords_message);
        mTvNext.setVisibility(View.GONE);
        mIvNext.setVisibility(View.VISIBLE);
        enableButton(false);
        ArrayList<Keyword> keywords = PeerMountainManager.getRandomKeywordsWithSavedIncluded(getActivity()).getKeywords();
//        myKeywords = PeerMountainManager.getSavedKeywordsObject();
        generateKeywords(keywords);
    }

    private void enableButton(boolean isEnabled) {
        btnRipple.setEnabled(isEnabled);
        flNext.setEnabled(isEnabled);
        mIvNext.setEnabled(isEnabled);
    }

    public void setViewForReset() {
        setToolbarForKeywords();
        mTvTitle.setText(R.string.pm_show_keywords_title);
        mTvMsg.setText(R.string.pm_saved_keywords_message);
        mTvNext.setVisibility(View.GONE);
        mIvNext.setVisibility(View.VISIBLE);
        ArrayList<Keyword> keywords = PeerMountainManager.getRandomKeywordsWithSavedIncluded(getActivity()).getKeywords();
        generateKeywords(keywords);
    }

    private void setToolbarForKeywords() {
        setToolbar(R.drawable.pm_ic_logo, R.string.pm_register_title, null);
        hideToolbar();
    }

    FrameLayout flNext;
    MaterialRippleLayout btnRipple;
    ImageView ivCheck;

    private void initView(View view) {
        mTvTitle = (PeerMountainTextView) view.findViewById(R.id.tvTitle);
        mTvMsg = (PeerMountainTextView) view.findViewById(R.id.tvMsg);
        mGridKeywords = (GridLayout) view.findViewById(R.id.gridKeywords);
        mLlTvWhy = view.findViewById(R.id.llTvWhy);
        mIvNext = (ImageView) view.findViewById(R.id.ivNext);
        mTvNext = (PeerMountainTextView) view.findViewById(R.id.tvNext);
        flNext = view.findViewById(R.id.flNext);
        ivCheck = view.findViewById(R.id.ivCheck);
    }

    private void setListeners() {
        flNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isJustShowing) {
                    isJustShowing = false;
                    setViewForValidate();
                } else {
                    onKeywordsConfirmed();
                }
            }
        });

        mLlTvWhy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onWhy();
                }
            }
        });

        btnRipple = RippleUtils.setRippleEffectSquare(flNext);
    }

    public interface OnFragmentInteractionListener {
        void onKeywordsSaved();
        void onWhy();
    }
}
