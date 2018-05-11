package com.peermountain.core.odk.views.widgets;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.widget.Button;

import com.peermountain.core.R;
import com.peermountain.core.views.PeerMountainTextView;

/**
 * Created by Galeen on 5/11/2018.
 */
public class EndWidget extends NestedScrollView {
    private Button btnSaveForm, btnSendForm;
    private PeerMountainTextView tvAnswers;
    public EndWidget(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.pm_end_widget, this);
        }
        tvAnswers = findViewById(R.id.tvAnswers);
        btnSaveForm = findViewById(R.id.btnSaveForm);
        btnSendForm = findViewById(R.id.btnSendForm);
    }

    public void setOnSaveFormListener(OnClickListener onClickListener){
        btnSaveForm.setOnClickListener(onClickListener);
    }

    public void setOnSendFormListener(OnClickListener onClickListener){
        btnSendForm.setOnClickListener(onClickListener);
    }

    public void performSendForm(){
        btnSendForm.performClick();
    }

    public void performSaveForm(){
        btnSaveForm.performClick();
    }

    public void setText(String text){
        tvAnswers.setText(text);
    }
}
