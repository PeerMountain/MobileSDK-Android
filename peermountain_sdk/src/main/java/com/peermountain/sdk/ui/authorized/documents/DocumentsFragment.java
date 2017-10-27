package com.peermountain.sdk.ui.authorized.documents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.home.CardsEventListener;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;

public class DocumentsFragment extends HomeToolbarFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    DocumentsHelper documentsHelper;

    public DocumentsFragment() {
        // Required empty public constructor
    }

    public static DocumentsFragment newInstance() {
        DocumentsFragment fragment = new DocumentsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
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
        documentsHelper = new DocumentsHelper(new DocumentsHelper.Events() {
            @Override
            public void refreshAdapter() {
                if(documentsAdapter!=null){
                    documentsAdapter.notifyDataSetChanged();
                    cardsEventListener.setCardsBackground();
                }
            }

            @Override
            public Activity getActivity() {
                return DocumentsFragment.this.getActivity();
            }

            @Override
            public Fragment getFragment() {
                return DocumentsFragment.this;
            }

            @Override
            public void onScanSDKLoading(boolean loading) {
                if(pmTvLoading!=null)
                pmTvLoading.setVisibility(!loading ?View.GONE:View.VISIBLE);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_documents, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        setUpView();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        documentsHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        documentsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    CardStackView documentCardsView;
TextView pmTvLoading;
    /**
     * type ff to fast get new views
     */
    private void getViews(View view) {
        documentCardsView = view.findViewById(R.id.pm_documents_card_stack_view);
        pmTvLoading = view.findViewById(R.id.pmTvLoading);
    }

    private void setUpView() {
        setToolbar(R.drawable.pm_ic_logo_white, R.drawable.pm_ic_plus_24dp, R.string.pm_title_documents,
                homeToolbarEvents != null ? homeToolbarEvents.getOpenMenuListener() : null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 10/17/2017 TBI add document
                    }
                });
        setCardsView();
    }

    private void setCardsView() {
        documentCardsView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                documentCardsView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setCards();
            }
        });
    }

    ArrayList<AppDocument> documents ;
    DocumentsAdapter documentsAdapter;
    CardsEventListener cardsEventListener;

    private void setCards() {
        documents = documentsHelper.getDocumentsForAdapter();
        documentsAdapter = new DocumentsAdapter(getContext(), new DocumentsAdapter.DocumentEvents() {
            @Override
            public void onUpdateDocumentClick(AppDocument document) {
                if(pmTvLoading.getVisibility()==View.VISIBLE) return;//it is loading already
                LogUtils.d("onUpdateDocumentClick", document.getTitle());
                documentsHelper.updateDocument(document);
            }
        });
        documentsAdapter.addAll(documents);
        documentCardsView.setAdapter(documentsAdapter);
        documentCardsView.setCardEventListener(cardsEventListener = new CardsEventListener(documents, documentsAdapter, documentCardsView));
    }

    public interface OnFragmentInteractionListener {
    }
}
