package com.peermountain.core.odk.views.widgets.choice;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.peermountain.core.odk.views.widgets.base.QuestionWidget;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galeen on 2/14/2018.
 */

public abstract class ChoiceList extends QuestionWidget {
    List<SelectChoice> items; // may take a while to compute

    ArrayList<CompoundButton> buttons;
    ViewGroup.LayoutParams buttonParams;
    ViewGroup answerView;

    public ChoiceList(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
        // SurveyCTO-added support for dynamic select content (from .csv files)
//        XPathFuncExpr xpathFuncExpr = ExternalDataUtil.getSearchXPathExpression(
//                prompt.getAppearanceHint());
//        if (xpathFuncExpr != null) {
//            items = ExternalDataUtil.populateExternalChoices(prompt, xpathFuncExpr);
//        } else {
        items = prompt.getSelectChoices();
//        }
        buttons = new ArrayList<>();
        answerView = getParentView();
        buttonParams = getButtonParams();
        initAnswer();

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                CompoundButton r = createButton(i);

                buttons.add(r);

                answerView.addView(r, buttonParams);
            }
        }
        addAnswerView(answerView);
    }
    protected abstract CompoundButton createButton(int position);

    protected abstract ViewGroup getParentView();

    protected abstract ViewGroup.LayoutParams getButtonParams();

    protected abstract void initAnswer();

    @Override
    public void setFocus(Context context) {
        defaultSetFocus();
    }

    @Override
    public boolean canGetFocus() {
        return false;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {

    }
}
