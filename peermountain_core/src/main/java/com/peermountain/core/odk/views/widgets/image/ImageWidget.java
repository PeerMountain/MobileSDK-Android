package com.peermountain.core.odk.views.widgets.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.odk.views.widgets.base.PermissionQuestionWidget;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.core.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 3/1/2018.
 */

public class ImageWidget extends PermissionQuestionWidget {
    private PeerMountainTextView tvButton;
    private ImageView imageView;
    private boolean waitingForData = false;
    private PmDocumentsHelper pmDocumentsHelper;
    private AppDocument appDocument = new AppDocument(true);

    public ImageWidget(final Activity activity, FormEntryPrompt prompt) {
        super(activity, prompt);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_image_widget, viewParent);
            tvButton = viewParent.findViewById(R.id.pmBtnSelectImage);
            imageView = viewParent.findViewById(R.id.pmIvSelectedImage);
//            if (getFormEntryPrompt().getAnswerValue() != null) {
//                answeredLocation = new GeoPointData((double[])getFormEntryPrompt().getAnswerValue().getValue());
//                tvLabel.setText(answeredLocation.getDisplayText());
//            }
            tvButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    waitingForData = pmDocumentsHelper.addDocument(appDocument);
                    tvButton.setEnabled(!waitingForData);
                }
            });
        }
        initDocumentHelper(activity);
    }

    public void initDocumentHelper(final Activity activity) {
        pmDocumentsHelper = new PmDocumentsHelper(
                new PmDocumentsHelper.Events() {
                    @Override
                    public void refreshAdapter() {
                        if(appDocument.getFileDocuments().size()>0){
                            FileDocument fileDocument = appDocument.getFileDocuments().get(0);
                            Picasso.with(activity).load(fileDocument.getImageUri())
                                    .error(R.color.pm_odk_text_error)
                                    .into(imageView);
                        }
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

    @Override
    public IAnswerData getAnswer() {
        return appDocument.isEmpty() ? null : new StringData(appDocument.getTitle());
    }

    @Override
    public void clearAnswer() {
        appDocument = new AppDocument(true);
        tvButton.setEnabled(true);
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
