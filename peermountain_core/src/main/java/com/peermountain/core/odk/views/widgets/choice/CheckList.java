package com.peermountain.core.odk.views.widgets.choice;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.peermountain.core.R;
import com.peermountain.core.odk.utils.ViewIds;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galeen on 2/14/2018.
 */

public class CheckList extends ChoiceList {
    LinearLayout linearLayout;
    private List<Selection> answer;

    public CheckList(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
    }

    @Override
    protected CompoundButton createButton(int position) {
        CheckBox checkBox = new CheckBox(new ContextThemeWrapper(getContext(), R.style.PmCheckbox)
                , null, 0);

        checkBox.setId(ViewIds.generateViewId());
        checkBox.setTag(position);
        setTextSize(checkBox, R.dimen.pm_text_normal);
        checkBox.setText(getFormEntryPrompt().getSelectChoiceText(items.get(position)));
        checkBox.setEnabled(!getFormEntryPrompt().isReadOnly());
        checkBox.setFocusable(!getFormEntryPrompt().isReadOnly());

        for (int vi = 0; vi < answer.size(); vi++) {
            // match based on value, not key
            if (items.get(position).getValue().equals(answer.get(vi).getValue())) {
                checkBox.setChecked(true);
                break;
            }
        }
        return checkBox;
    }

    @Override
    protected ViewGroup getParentView() {
        linearLayout = new RadioGroup(getContext());
        linearLayout.setOrientation(VERTICAL);
        return linearLayout;
    }

    @Override
    protected ViewGroup.LayoutParams getButtonParams() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initAnswer() {
        answer = new ArrayList<>();
        if (getFormEntryPrompt().getAnswerValue() != null) {
            //noinspection unchecked
            answer = (List<Selection>) getFormEntryPrompt().getAnswerValue().getValue();
        } else {
            answer = new ArrayList<>();
        }
    }

    @Override
    public IAnswerData getAnswer() {
        List<Selection> selections = new ArrayList<>();
        for (int i = 0; i < buttons.size(); ++i) {
            CompoundButton button = buttons.get(i);
            if (button.isChecked()) {
                selections.add(new Selection(items.get(i)));
            }
        }

        return selections.size() == 0 ? null : new SelectMultiData(selections);
    }

    @Override
    public void clearAnswer() {
        for (CompoundButton button : buttons) {
            button.setChecked(false);
        }
    }
}
