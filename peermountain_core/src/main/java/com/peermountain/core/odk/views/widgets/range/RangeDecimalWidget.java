package com.peermountain.core.odk.views.widgets.range;

import android.content.Context;

import org.javarosa.core.model.data.DecimalData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 2/13/2018.
 */

public class RangeDecimalWidget extends RangeWidget {
    public RangeDecimalWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
    }

    @Override
    public IAnswerData getAnswer() {
        return actualValue != null
                ? new DecimalData(actualValue.doubleValue())
                : null;
    }

    @Override
    protected void setUpActualValueLabel() {
        String value = actualValue != null
                ? String.valueOf(actualValue.doubleValue())
                : "";

        if (tvHelp != null) {
            tvHelp.setText(value);
        }
    }
}
