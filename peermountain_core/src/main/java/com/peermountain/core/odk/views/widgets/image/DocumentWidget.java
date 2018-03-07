package com.peermountain.core.odk.views.widgets.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.odk.views.widgets.base.PermissionQuestionWidget;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.core.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 3/1/2018.
 */

public class DocumentWidget extends PermissionQuestionWidget {
    private PeerMountainTextView tvPickBtn, tvSelectDocumentBtn;
    private ImageView imageView;
    private boolean waitingForData = false;
    private PmDocumentsHelper pmDocumentsHelper;
    private AppDocument appDocument = new AppDocument(true), waitingDocument;

    public DocumentWidget(FragmentActivity activity, FormEntryPrompt prompt) {
        super(activity, prompt);
        createView(prompt);
        initDocumentHelper();
    }

    public void createView(FormEntryPrompt prompt) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_image_widget, viewParent);
            tvPickBtn = viewParent.findViewById(R.id.pmBtnSelectImage);
            tvSelectDocumentBtn = viewParent.findViewById(R.id.pmBtnSelectDocument);
            imageView = viewParent.findViewById(R.id.pmIvSelectedImage);
            setCurrentAnswer(prompt);
            setListeners();
        }
    }

    DocumentsFragmentDialog dialog;
    private void setListeners() {
        tvPickBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appDocument.isEmpty()){
                    waitingForData = pmDocumentsHelper.addDocument(appDocument);
                }else{
                    waitingDocument = new AppDocument(true);
                    waitingDocument.setShouldAdd(true);
                    waitingForData = pmDocumentsHelper.addDocument(waitingDocument);
                }
                tvPickBtn.setEnabled(!waitingForData);
            }
        });

        tvSelectDocumentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new DocumentsFragmentDialog();
                dialog.setListener(new AppDocumentsAdapter.Events() {
                    @Override
                    public void onDocumentSelected(AppDocument document) {
                        if (document != null) {
                            appDocument = document;
                            loadImage();
                        }
                        dialog.dismiss();
                        setButtonTitle();
                    }
                });
                dialog.show(activity.getSupportFragmentManager(), "documents_dialog");
            }
        });
    }

    public void setCurrentAnswer(FormEntryPrompt prompt) {
        if (prompt.getAnswerValue() != null) {
            String id = prompt.getAnswerText();
            appDocument = PeerMountainManager.getDocument(id);
            if (appDocument == null) {
                appDocument = new AppDocument(true);
                appDocument.setShouldAdd(true);
            } else {
                loadImage();
            }
        }
    }

    public void initDocumentHelper() {
        pmDocumentsHelper = new PmDocumentsHelper(
                new PmDocumentsHelper.Events() {
                    @Override
                    public void refreshAdapter() {
                        //this one will be called after a new file is selected and document created
                        if(waitingDocument!=null){
                            appDocument = waitingDocument;
                            waitingDocument = null;
                        }
                        loadImage();
                        setButtonTitle();
                    }

                    @Override
                    public Activity getActivity() {
                        return activity;
                    }

                    @Override
                    public Fragment getFragment() {
                        return null;
                    }

                    @Override
                    public void onScanSDKLoading(boolean loading) {

                    }

                    @Override
                    public void onAddingDocumentCanceled(AppDocument document) {
                        clearAnswer();
                    }
                }
        );
    }

    public void loadImage() {
        if (appDocument.getFileDocuments().size() > 0) {
            FileDocument fileDocument = appDocument.getFileDocuments().get(0);
            Picasso.with(activity).load(fileDocument.getImageUri())
                    .error(R.color.pm_odk_text_error)
                    .resize(imageView.getWidth(),activity.getResources().getDimensionPixelSize(R.dimen.pm_odk_image_document_height))
                    .into(imageView);
            setButtonTitle();
        }
    }

    private void setButtonTitle() {
        if(appDocument!=null && !appDocument.isEmpty()) {
            tvPickBtn.setText(activity.getString(R.string.pm_change_file_btn, appDocument.getTitle()));
        }else{
            tvPickBtn.setText(activity.getString(R.string.pm_pick_file_btn));
        }
        tvPickBtn.setEnabled(true);
    }

    @Override
    public IAnswerData getAnswer() {
        return appDocument.isEmpty() ? null : new StringData(appDocument.getId());
    }

    @Override
    public void clearAnswer() {
        appDocument = new AppDocument(true);
        appDocument.setShouldAdd(true);
        setButtonTitle();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return waitingForData && pmDocumentsHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setFocus(Context context) {

    }

    @Override
    public boolean canGetFocus() {
        return false;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {

    }
}
