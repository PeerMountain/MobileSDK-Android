package com.peermountain.core.odk.views.widgets.image;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Galeen on 3/6/18.
 */

public class AppDocumentsAdapter extends RecyclerView.Adapter<AppDocumentsAdapter.ViewHolder> {

    private ArrayList<AppDocument> docs;
    private Events mListener;
    private int imageSize;


    public AppDocumentsAdapter(ArrayList<AppDocument> docs, Events mListener) {
        this.docs = new ArrayList<>(docs);
        for (AppDocument document : this.docs) {
            if(document.isEmpty()){
                this.docs.remove(document);
                break;
            }
        }
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pm_select_document_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(docs.get(position));
    }

    @Override
    public int getItemCount() {
        return docs == null ? 0 : docs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDoc;
        private final PeerMountainTextView tvDocumentTitle;
        private AppDocument appDocument;

        ViewHolder(View itemView) {
            super(itemView);
            if (imageSize == 0) {
                imageSize = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.odk_doc_list_image_size);
            }
            tvDocumentTitle = itemView.findViewById(R.id.tvDocumentTitle);
            ivDoc = itemView.findViewById(R.id.ivDoc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDocumentSelected(appDocument);
                    }
                }
            });
        }

        void bind(AppDocument document) {
            appDocument = document;
            if (TextUtils.isEmpty(document.getTitle())) {
                tvDocumentTitle.setText(R.string.pm_document_item_id_title);
            }else{
                tvDocumentTitle.setText(document.getTitle());
            }
            loadDocumentImage(document,ivDoc,imageSize);
        }


    }

    public static String loadDocumentImage(AppDocument document, ImageView ivDoc, int imageSize) {
        String title;
        if (TextUtils.isEmpty(document.getTitle())) {
            title = ivDoc.getContext().getString(R.string.pm_document_item_id_title);
        }else{
            title = document.getTitle();
        }
        if (document.getRes() != 0) {
            Picasso.with(ivDoc.getContext())
                    .load(document.getRes() )
                    .resize(0, imageSize)
                    .into(ivDoc);
            return title;
        }

        String filePath = null;
        if (document.isIdentityDocument() && document.getDocuments().get(0).getImageCropped()!=null) {
            filePath = document.getDocuments().get(0).getImageCropped().getImageUri();
        } else if (document.getFileDocuments() != null && document.getFileDocuments().size() > 0) {
            filePath = document.getFileDocuments().get(0).getImageUri();
        }
        if (filePath != null) {
            Picasso.with(ivDoc.getContext())
                    .load(filePath)
                    .resize(0, imageSize)
                    .into(ivDoc);
        }
        return title;
    }

    public interface Events {
        void onDocumentSelected(AppDocument document);
    }
}
