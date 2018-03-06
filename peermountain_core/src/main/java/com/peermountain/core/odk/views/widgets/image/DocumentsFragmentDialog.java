package com.peermountain.core.odk.views.widgets.image;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peermountain.core.R;
import com.peermountain.core.persistence.PeerMountainManager;

public class DocumentsFragmentDialog extends DialogFragment {

    private AppDocumentsAdapter.Events mListener;

    public DocumentsFragmentDialog() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents_fragment_dialog, container, false);
    }

    RecyclerView rvDocuments;
    AppDocumentsAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvDocuments = view.findViewById(R.id.rvDocuments);
        rvDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppDocumentsAdapter(PeerMountainManager.getDocuments(),
                mListener);
        rvDocuments.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(AppDocumentsAdapter.Events listener) {
        this.mListener = listener;
    }
}
