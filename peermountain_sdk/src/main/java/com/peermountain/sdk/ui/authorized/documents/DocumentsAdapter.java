package com.peermountain.sdk.ui.authorized.documents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Document;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Galeen on 10/12/2017.
 */

public class DocumentsAdapter extends ArrayAdapter<AppDocument> {

    public DocumentsAdapter(Context context) {
        super(context, 0);

    }

    private boolean showActivity = true;
    private boolean setAsFirst = true;
    public void setShowActivity(boolean showActivity) {
        this.showActivity = showActivity;
        setAsFirst = true;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.pm_document_tinder_card_view, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        if (position == 0 || setAsFirst) {//first time when the adapter is created to set the bkg white
            setAsFirst = false;
            holder.parent.setBackgroundResource(R.drawable.pm_card_white);
        } else {
            holder.parent.setBackgroundResource(R.drawable.pm_card);
        }

        holder.bind(getItem(position), showActivity);

        return contentView;
    }

    private static class ViewHolder {
        final View parent, btnRipple;
        final TextView tvMsg,btn;
        final ImageView ivDocumentBack, ivDocument;
        AppDocument appDocument;

        ViewHolder(View view) {
            parent = view;
            tvMsg = (TextView) view.findViewById(R.id.tvMsg);
            ivDocumentBack = (ImageView) view.findViewById(R.id.ivPmFullImageBack);
            ivDocument = view.findViewById(R.id.ivPmFullImage);
            btn = view.findViewById(R.id.pmTvUpdate);
            btnRipple = RippleUtils.setRippleEffectSquare(btn);
            btn.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View clickedView) {

                }
            });
        }

        void bind(AppDocument document, boolean isActivity) {
            appDocument = document;
            if (appDocument == null) return;
            if (!appDocument.isEmpty) {
                btnRipple.setEnabled(true);
                btn.setEnabled(true);
                tvMsg.setText(R.string.pm_document_item_id_title);
                Document id;
                if(appDocument.getDocuments().size()>0
                        && (id = appDocument.getDocuments().get(0))!=null) {
                    if(id.getImageCropped()!=null) {
                        String uri = id.getImageCropped().getImageUri();
                        loadImage(uri, ivDocument);
                    }
                    if(id.getImageCroppedBack()!=null) {
                        String uriBack = id.getImageCroppedBack().getImageUri();
                        loadImage(uriBack, ivDocumentBack);
                    }
                }else{
                    ivDocument.setVisibility(View.GONE);
                    ivDocumentBack.setVisibility(View.GONE);
                }
            } else {
                btn.setEnabled(false);
                btnRipple.setEnabled(false);
                ivDocument.setVisibility(View.GONE);
                ivDocumentBack.setVisibility(View.GONE);
                tvMsg.setText(R.string.pm_document_item_empty);
            }
        }

        private void loadImage(String uri, ImageView iv) {
            if (uri != null) {
                iv.setVisibility(View.VISIBLE);
                Picasso.with(parent.getContext())
                        .load(uri)
//                        .placeholder(R.drawable.pm_profil_white)
                        .error(R.color.pm_error_loading_avatar)
                        .into(iv);
            }else{
                iv.setVisibility(View.GONE);
            }
        }
    }
}
