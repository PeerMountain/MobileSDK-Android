package com.peermountain.core.odk.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peermountain.core.R;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.utils.ViewIds;
import com.peermountain.core.odk.views.widgets.WidgetFactory;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Galeen on 1/24/2018.
 * ScrollView holding questions of one group or single question
 */

public class ODKView extends NestedScrollView implements View.OnTouchListener {
    public static final String FIELD_LIST = "field-list";

    private LinearLayout view;
    private LinearLayout.LayoutParams layout;
    private ArrayList<QuestionWidget> widgets;
    private int paddingSmall = 10,padding;
    public FormEntryPrompt[] questionPrompts;

    public ODKView(Activity activity, FormEntryPrompt[] questionPrompts,
                   FormEntryCaption[] groups, boolean advancingPage) {
        super(activity);
        this.questionPrompts = questionPrompts;
        widgets = new ArrayList<>();

        view = new LinearLayout(getContext());
        view.setOrientation(LinearLayout.VERTICAL);
        view.setGravity(Gravity.TOP);
        padding = getContext().getResources().getDimensionPixelSize(R.dimen.pm_margin_normal);
        paddingSmall = getContext().getResources().getDimensionPixelSize(R.dimen.pm_margin_small);
        view.setPadding(0, paddingSmall, 0, 0);

        layout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(paddingSmall, 0, paddingSmall, 0);

        // display which group you are in as well as the question
        addGroupText(groups);

        // when the grouped fields are populated by an external app, this will get true.
        boolean readOnlyOverride = false;

        // get the group we are showing -- it will be the last of the groups in the groups list
//        if (groups != null && groups.length > 0) {
//            final FormEntryCaption c = groups[groups.length - 1];
//            final String intentString = c.getFormElement().getAdditionalAttribute(null, "intent");
//            if (intentString != null && intentString.length() != 0) {
//
//                readOnlyOverride = true;
//
//                final String buttonText;
//                final String errorString;
//                String v = c.getSpecialFormQuestionText("buttonText");
//                buttonText = (v != null) ? v : context.getString(R.string.pm_launch_app);
//                v = c.getSpecialFormQuestionText("noAppErrorString");
//                errorString = (v != null) ? v : context.getString(R.string.pm_no_app);
//
//                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
//                params.setMargins(7, 5, 7, 5);
//
//                // set button formatting
//                Button launchIntentButton = new Button(getContext());
//                launchIntentButton.setId(ViewIds.generateViewId());
//                launchIntentButton.setText(buttonText);
////                launchIntentButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
////                        Collect.getQuestionFontSize() + 2);
//                launchIntentButton.setPadding(20, 20, 20, 20);
//                launchIntentButton.setLayoutParams(params);
//
//                launchIntentButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        String intentName = ExternalAppsUtils.extractIntentName(intentString);
////                        Map<String, String> parameters = ExternalAppsUtils.extractParameters(
////                                intentString);
////
////                        Intent i = new Intent(intentName);
////                        try {
////                            ExternalAppsUtils.populateParameters(i, parameters,
////                                    c.getIndex().getReference());
////
////                            for (FormEntryPrompt p : questionPrompts) {
////                                IFormElement formElement = p.getFormElement();
////                                if (formElement instanceof QuestionDef) {
////                                    TreeReference reference =
////                                            (TreeReference) formElement.getBind().getReference();
////                                    IAnswerData answerValue = p.getAnswerValue();
////                                    Object value =
////                                            answerValue == null ? null : answerValue.getValue();
////                                    switch (p.getDataType()) {
////                                        case Constants.DATATYPE_TEXT:
////                                        case Constants.DATATYPE_INTEGER:
////                                        case Constants.DATATYPE_DECIMAL:
////                                            i.putExtra(reference.getNameLast(),
////                                                    (Serializable) value);
////                                            break;
////                                    }
////                                }
////                            }
////
////                            ((Activity) getContext()).startActivityForResult(i, RequestCodes.EX_GROUP_CAPTURE);
////                        } catch (ExternalParamsException e) {
////                            Timber.e(e, "ExternalParamsException");
////
////                            ToastUtils.showShortToast(e.getMessage());
////                        } catch (ActivityNotFoundException e) {
////                            Timber.e(e, "ActivityNotFoundExcept");
////
////                            ToastUtils.showShortToast(errorString);
////                        }
//                    }
//                });
//
//                View divider = new View(getContext());
//                divider.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
//                divider.setMinimumHeight(3);
//                view.addView(divider);
//
//                view.addView(launchIntentButton, layout);
//            }
//        }

        boolean first = true;
        for (FormEntryPrompt p : questionPrompts) {
            if (!first) {
                addDivider();
            } else {
                first = false;
            }

            // if question or answer type is not supported, use text widget
            try {
                QuestionWidget qw = WidgetFactory.createWidgetFromPrompt(p, activity, readOnlyOverride);
                qw.setLongClickable(true);
//            qw.setOnLongClickListener(this);
                qw.setId(ViewIds.generateViewId());

                widgets.add(qw);
                view.addView(qw, layout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addView(view);
    }

    private void addDivider() {// no divider for now
//        View divider = new View(getContext());
//        divider.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
//        divider.setMinimumHeight(3);
//        view.addView(divider,layout);
    }

    /**
     * // * Add a TextView containing the hierarchy of groups to which the question belongs. //
     */
    private void addGroupText(FormEntryCaption[] groups) {
        String path = getGroupsPath(groups);

        // build view
        if (!path.isEmpty()) {
            TextView tv = new TextView(getContext());
            tv.setText(path);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getContext().getResources().getDimension(R.dimen.pm_text_big));
            tv.setPadding(padding, 0, padding, paddingSmall);
            view.addView(tv, layout);
        }
    }

    @NonNull
    public static String getGroupsPath(FormEntryCaption[] groups) {
        StringBuilder path = new StringBuilder("");
        if (groups != null) {
            String longText;
            int multiplicity;
            int index = 1;
            // list all groups in one string
            for (FormEntryCaption group : groups) {
                multiplicity = group.getMultiplicity() + 1;
                longText = group.getLongText();
                if (longText != null) {
                    path.append(longText);
                    if (group.repeats() && multiplicity > 0) {
                        path
                                .append(" (")
                                .append(multiplicity)
                                .append(")");
                    }
                    if (index < groups.length) {
                        path.append(" > ");
                    }
                    index++;
                }
            }
        }

        return path.toString();
    }

    public void setFocus(Context context) {
//        for (int i = 0; i < widgets.size(); i++) {
//            if(widgets.get(i).canGetFocus()){
//                widgets.get(i).setFocus(context);
//                break;
//            }
//        }
        if (widgets.size() > 0) {
            widgets.get(0).setFocus(context);
        }
    }

    /**
     * Releases widget resources, such as {@link android.media.MediaPlayer}s
     */
    public void releaseWidgetResources() {
        for (QuestionWidget w : widgets) {
            w.release();
        }
    }

    public ArrayList<QuestionWidget> getWidgets() {
        return widgets;
    }

    /**
     * @return a HashMap of answers entered by the user for this set of widgets
     */
    public HashMap<FormIndex, IAnswerData> getAnswers() {
        HashMap<FormIndex, IAnswerData> answers = new LinkedHashMap<>();
        for (QuestionWidget q : widgets) {
            /*
             * The FormEntryPrompt has the FormIndex, which is where the answer gets stored. The
             * QuestionWidget has the answer the user has entered.
             */
            FormEntryPrompt p = q.getFormEntryPrompt();
            answers.put(p.getIndex(), q.getAnswer());
        }

        return answers;
    }

    public void onAnswerQuestion(FormController.FailedConstraint constraint) {
        int size = widgets.size();
        for (int i = 0; i < size; i++) {
            widgets.get(i).onAnswerQuestion(constraint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        onTouch(this,event);
        return false;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    /**
     * @param requestCode param from Activity result
     * @param resultCode param from Activity result
     * @param data param from Activity result
     * @return true if is handled
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data){
        for (QuestionWidget widget : widgets) {
            if(widget.onActivityResult(requestCode, resultCode, data)) return true;
        }
        return false;
    }
}
