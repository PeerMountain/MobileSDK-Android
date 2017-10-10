package com.peermountain.sdk.ui.register;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.balysv.materialripple.MaterialRippleLayout;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.SystemHelper;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.peermountain.sdk.views.PeerMountainTextView;

public class RegisterKeywordsFragment extends ToolbarFragment {

    private OnFragmentInteractionListener mListener;
    private PeerMountainTextView pmTvMessageKeywords;
    private EditText pmEtKeywords;
    private ImageView pmIvValid;
    private LinearLayout pmLlAddKeywords;
    private PeerMountainTextView pmTVSavedKeywords;
    private PeerMountainTextView pmTVSavedKeywordsHint;

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
        PeerMountainManager.saveKeywords(null);//remove current
        initView(view);
        setListeners();
        setCheckBtn(false);
        showAddKeywords();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setToolbarForKeywords() {
        setToolbar(R.drawable.pm_ic_logo,R.string.pm_register_title,null);
    }

    private void setToolbarForKeywordsResult() {
        setToolbar(-1,R.string.pm_show_keywords_title,null);
    }

    private void initView(View view) {
        pmTvMessageKeywords = (PeerMountainTextView) view.findViewById(R.id.pmTvMessageKeywords);
        pmEtKeywords = (EditText) view.findViewById(R.id.pmEtKeywords);
        pmIvValid = (ImageView) view.findViewById(R.id.pmIvValid);
        pmLlAddKeywords = (LinearLayout) view.findViewById(R.id.pmLlAddKeywords);
        pmTVSavedKeywords = (PeerMountainTextView) view.findViewById(R.id.pmTVSavedKeywords);
        pmTVSavedKeywordsHint = (PeerMountainTextView) view.findViewById(R.id.pmTVSavedKeywordsHint);
    }

    MaterialRippleLayout btnRipple;

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        pmEtKeywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkWords();
            }
        });
        pmIvValid.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (!isShowingKeywordsResult) {
                    prepareKeywords();
                } else {
                    PeerMountainManager.saveKeywords(keywords);
                    if (mListener != null)
                        mListener.onKeywordsSaved();
                }
            }
        });
        btnRipple = RippleUtils.setRippleEffectSquare(pmIvValid);
    }

    boolean isShowingKeywordsResult = false;
    String keywords = null;

    private void prepareKeywords() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbToShow = new StringBuilder();
        String[] words = pmEtKeywords.getText().toString().split(",");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            if (word.length() > 0) {//not empty
                sb.append(word);
                sbToShow.append(word);
                if (i < words.length - 1) {
                    sb.append(",");
                    sbToShow.append(", ");
                }
            } else {
                if (i == words.length - 1) {
                    sb.delete(sb.length() - 1, sb.length());
                    sbToShow.delete(sbToShow.length() - 2, sbToShow.length());
                }
            }
        }
        keywords = sb.toString();
        showKeywords(sbToShow.toString());
    }

    private void showKeywords(String keywords) {
        SystemHelper.hideKeyboard(getActivity(), pmEtKeywords);
        pmLlAddKeywords.setVisibility(View.GONE);
        pmTvMessageKeywords.setVisibility(View.GONE);

        pmTVSavedKeywordsHint.setVisibility(View.VISIBLE);
        pmTVSavedKeywords.setVisibility(View.VISIBLE);
        setToolbarForKeywordsResult();
        pmTVSavedKeywords.setText(keywords);
        isShowingKeywordsResult = true;
    }

    @Override
    public boolean onBackPressed() {
        if (isShowingKeywordsResult) {
            showAddKeywords();
            return true;
        }
        return false;
    }

    private void showAddKeywords() {
        pmLlAddKeywords.setVisibility(View.VISIBLE);
        pmTvMessageKeywords.setVisibility(View.VISIBLE);
        pmEtKeywords.requestFocus();
        SystemHelper.showKeyboard(getActivity(), pmEtKeywords);

        pmTVSavedKeywordsHint.setVisibility(View.GONE);
        pmTVSavedKeywords.setVisibility(View.GONE);
        setToolbarForKeywords();
        isShowingKeywordsResult = false;
    }

    private void checkWords() {
        if (pmEtKeywords.getText().length() > 0) {
            String[] words = pmEtKeywords.getText().toString().split(",");
            int emptyWordsCount = 0;
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].trim().length() == 0) emptyWordsCount++;//check for blank as ", ,"
            }
            setCheckBtn(words.length - emptyWordsCount >= 4);// at least 4 words divided by ,
        } else {
            setCheckBtn(false);
        }
    }

    private void setCheckBtn(boolean enabled) {
        btnRipple.setEnabled(enabled);
        pmIvValid.setEnabled(enabled);
    }

    public interface OnFragmentInteractionListener {
        void onKeywordsSaved();
    }
}
