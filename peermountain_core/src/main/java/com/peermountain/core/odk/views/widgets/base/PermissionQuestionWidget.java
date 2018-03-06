package com.peermountain.core.odk.views.widgets.base;

import android.support.v4.app.FragmentActivity;

import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 3/1/2018.
 * Question widget with permission callback
 */

public abstract class PermissionQuestionWidget extends QuestionWidget {
    public FragmentActivity activity;
    public Events activityCallback;

    public PermissionQuestionWidget(FragmentActivity activity, FormEntryPrompt prompt) {
        super(activity, prompt);
        this.activity = activity;
        if (activity instanceof Events) {
            activityCallback = (Events) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " QuestionWidget.Events");
        }
    }
}
