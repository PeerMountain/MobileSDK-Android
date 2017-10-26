package com.peermountain.sdk.ui.authorized.documents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.FileUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.home.CardsEventListener;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DocumentsFragment extends HomeToolbarFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQUEST_CODE_SELECT_FILE = 533;

    private OnFragmentInteractionListener mListener;

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
    File localFile;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    LogUtils.d("selected file", data.getData().toString());
                    try {
                        ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(data.getData(), "r");
                        String ext = "pdf";
                        if(FileUtils.checkMimeType(getActivity(),data.getData(), "image/*")){
                            ext = "jpg";
                        }
                        LogUtils.d("selected file","extension : "+ext);
//                        localFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_IMAGES);
//                        FileUtils.copyFileAsync(pfd, localFile, new FileUtils.CopyFileEvents() {
//                            @Override
//                            public void onFinish(boolean isSuccess) {
//                                LogUtils.d("selected file", "onFinish " + isSuccess);
//                                if (isSuccess) {
//                                    updateDocument();
//                                }else {
//                                    showUpdateFileError();
//                                }
//                            }
//                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        showUpdateFileError();
                    }
                }
                break;

        }
    }
    private void showUpdateFileError(){
        Toast.makeText(getContext(), "There was a problem", Toast.LENGTH_SHORT).show();
    }

    public void updateDocument() {
        if(documentToUpdate==null  || localFile==null)
            return;
        FileDocument fileDocument;
        if(documentToUpdate.getFileDocuments().size()>0
                && documentToUpdate.getFileDocuments().get(0)!=null){
            fileDocument = documentToUpdate.getFileDocuments().get(0);
        }else{
            fileDocument = new FileDocument();
            documentToUpdate.getFileDocuments().add(fileDocument);
            documentToUpdate.setRes(0);
        }
        Uri uri = Uri.fromFile(localFile);

        if(FileUtils.getExtension(uri.toString()).equalsIgnoreCase("pdf")) {
            fileDocument.setType(FileDocument.TYPE_PDF);
        }else{
            fileDocument.setType(FileDocument.TYPE_IMAGE);
        }
        fileDocument.setUri(uri.toString());

        if(documentsAdapter!=null) {
            documentsAdapter.notifyDataSetChanged();
        }
        PeerMountainManager.updateDocument(documentToUpdate);
    }

    CardStackView documentCardsView;

    /**
     * type ff to fast get new views
     */
    private void getViews(View view) {
        documentCardsView = view.findViewById(R.id.pm_documents_card_stack_view);
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

    ArrayList<AppDocument> documents = new ArrayList<AppDocument>();
    DocumentsAdapter documentsAdapter;
    AppDocument documentToUpdate;

    private void setCards() {
        if (documents == null) return;
        addStaticDocuments();
        documentsAdapter = new DocumentsAdapter(getContext(), new DocumentsAdapter.DocumentEvents() {
            @Override
            public void onUpdateDocumentClick(AppDocument document) {
                LogUtils.d("onUpdateDocumentClick", document.getTitle());
                documentToUpdate = document;
                if (document.isIdentityDocument()) {

                } else {
                    PmCoreUtils.browseDocuments(getActivity(),REQUEST_CODE_SELECT_FILE);
                }
            }
        });
        documentsAdapter.addAll(documents);
        documentCardsView.setAdapter(documentsAdapter);
        documentCardsView.setCardEventListener(new CardsEventListener(documents, documentsAdapter, documentCardsView));
    }

    private void addStaticDocuments() {
        ArrayList<AppDocument> docs = PeerMountainManager.getDocuments();
        if(docs!=null && docs.size()>0){//we have saved docs
            documents.addAll(docs);
//            PeerMountainManager.saveDocuments(null);
        }else {
            AppDocument myID = new AppDocument(getString(R.string.pm_document_item_id_title));
            Profile me = PeerMountainManager.getProfile();
            if (me != null && me.getDocuments().size() > 0) {
                myID.getDocuments().add(me.getDocuments().get(0));
            }
            documents.add(myID);
            documents.add(new AppDocument(R.drawable.pm_birther, "Birth Certificate"));
            documents.add(new AppDocument(R.drawable.pm_employment_contract, "Employment Contract"));
            documents.add(new AppDocument(R.drawable.pm_income_tax, "Tax Return"));
            PeerMountainManager.saveDocuments(documents);
        }
    }


    public interface OnFragmentInteractionListener {
    }
}
