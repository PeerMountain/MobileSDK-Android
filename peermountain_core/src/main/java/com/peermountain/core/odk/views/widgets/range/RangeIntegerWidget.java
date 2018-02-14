package com.peermountain.core.odk.views.widgets.range;

import android.content.Context;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 2/13/2018.
 */

public class RangeIntegerWidget extends RangeWidget {


    public RangeIntegerWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
    }

    @Override
    public IAnswerData getAnswer() {
        return actualValue == null ? null : new IntegerData(actualValue.intValue());
    }

    @Override
    public void setUpActualValueLabel() {
        String value = actualValue != null ? String.valueOf(actualValue.intValue()) : "";

        if (tvHelp != null) {
            tvHelp.setText(value);
        }
    }
}
