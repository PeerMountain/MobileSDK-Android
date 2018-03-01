package com.peermountain.core.odk.views.widgets.base;

import android.app.Activity;

import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 3/1/2018.
 * Question widget with permission callback
 */

public abstract class PermissionQuestionWidget extends QuestionWidget {
    public Activity activity;
    public Events activityCallback;

    public PermissionQuestionWidget(Activity activity, FormEntryPrompt prompt) {
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
