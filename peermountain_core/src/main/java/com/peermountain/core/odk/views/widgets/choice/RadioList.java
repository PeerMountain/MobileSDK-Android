package com.peermountain.core.odk.views.widgets.choice;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.peermountain.core.R;
import com.peermountain.core.odk.utils.ViewIds;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 2/14/2018.
 */

public class RadioList extends ChoiceList {
    RadioGroup buttonsRadioLayout;
    String answer = null;
    public RadioList(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
    }

    @Override
    protected CompoundButton createButton(int position) {
        RadioButton radioButton = new RadioButton(new ContextThemeWrapper(getContext(), R.style.PmRadioButton)
                , null, 0);

        radioButton.setId(ViewIds.generateViewId());
        radioButton.setTag(position);
        setTextSize(radioButton, R.dimen.pm_text_normal);
        radioButton.setText(getFormEntryPrompt().getSelectChoiceText(items.get(position)));
        radioButton.setEnabled(!getFormEntryPrompt().isReadOnly());
        radioButton.setFocusable(!getFormEntryPrompt().isReadOnly());
        if (items.get(position).getValue().equals(answer)) {
            radioButton.setChecked(true);
        }
        return radioButton;
    }

    @Override
    protected ViewGroup getParentView() {
        buttonsRadioLayout = new RadioGroup(getContext());
        buttonsRadioLayout.setOrientation(VERTICAL);
        return buttonsRadioLayout;
    }

    @Override
    protected ViewGroup.LayoutParams getButtonParams() {
        return new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initAnswer() {
        if (getFormEntryPrompt().getAnswerValue() != null) {
            answer = ((Selection) getFormEntryPrompt().getAnswerValue().getValue()).getValue();
        }
    }

    @Override
    public IAnswerData getAnswer() {
        int checkedRadioButtonId = buttonsRadioLayout.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            return null;
        } else {
            SelectChoice selectChoice = null;
            for (int i = buttons.size() - 1; i >= 0; i--) {
                RadioButton radioButton = (RadioButton) buttons.get(i);
                if (radioButton.getId() == checkedRadioButtonId) {
                    selectChoice = items.get((Integer) radioButton.getTag());
                    break;
                }
            }
            return selectChoice == null ? null : new SelectOneData(new Selection(selectChoice));
        }
    }

    @Override
    public void clearAnswer() {
        buttonsRadioLayout.clearCheck();
    }
}
