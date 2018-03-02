/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.peermountain.core.odk.views.widgets.edit_text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.peermountain.core.R;
import com.peermountain.core.odk.utils.TextUtils;
import com.peermountain.core.odk.utils.Timber;
import com.peermountain.core.odk.utils.ViewIds;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * The most basic widget that allows for entry of any text.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
@SuppressLint("ViewConstructor")
public class StringWidget extends QuestionWidget {
    private static final String ROWS = "rows";
    private EditText answerText;
    boolean readOnly = false;

    public StringWidget(Context context, FormEntryPrompt prompt, boolean readOnlyOverride) throws Exception {
        this(context, prompt, readOnlyOverride, true);
        setupChangeListener();
    }

    protected StringWidget(Context context, FormEntryPrompt prompt, boolean readOnlyOverride,
                           boolean derived) throws Exception {
        super(context, prompt);
        readOnly = prompt.isReadOnly() || readOnlyOverride;
        if (!readOnly) {
            setEditableView();
        } else {
            setStaticView();
        }
        if (answerText == null) {
            throw new Exception("");
        }
        mainInit(prompt);

    }

    private void mainInit(FormEntryPrompt prompt) {
        // capitalize the first letter of the sentence
        answerText.setKeyListener(new TextKeyListener(Capitalize.SENTENCES, false));

        // needed to make long read only text scroll
        answerText.setHorizontallyScrolling(false);
        answerText.setSingleLine(false);

         /*
         * If a 'rows' attribute is on the input tag, set the minimum number of lines
         * to display in the field to that value.
         *
         * I.e.,
         * <input ref="foo" rows="5">
         *   ...
         * </input>
         *
         * will set the height of the EditText box to 5 rows high.
         */
        String height = prompt.getQuestion().getAdditionalAttribute(null, ROWS);
        if (height != null && height.length() != 0) {
            try {
                int rows = Integer.parseInt(height);
                answerText.setMinLines(rows);
                answerText.setGravity(
                        Gravity.TOP); // to write test starting at the top of the edit area
            } catch (Exception e) {
                Timber.e("Unable to process the rows setting for the answerText field: %s", e.toString());
            }
        }
        String s = prompt.getAnswerText();
        if (!android.text.TextUtils.isEmpty(s)) {
            if(readOnly){
                answerText.setText(TextUtils.textToHtml(s));
            }else{
                answerText.setText(s);
            }
            Selection.setSelection(answerText.getText(), answerText.getText().toString().length());
        }
    }

    private void setStaticView() {
        answerText = new EditText(getContext());
        answerText.setId(ViewIds.generateViewId());
        setTextSize(answerText, R.dimen.pm_text_normal);

        answerText.setPadding(padding, 0, padding, 0);
        answerText.setBackground(null);
        answerText.setEnabled(false);
        answerText.setTextColor(getColor(R.color.pm_odk_text));
        answerText.setFocusable(false);
        addAnswerView(answerText);
    }

    private View vLine;

    private void setEditableView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_input_view, viewParent);
            answerText = viewParent.findViewById(R.id.pmEtInput);
            vLine = viewParent.findViewById(R.id.pmEtInputLine);
            answerText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    StringWidget.this.onFocus(hasFocus);
                    if (isWithError) return;
                    if (hasFocus) {
                        updateLineColor(colorActive);
                    } else {
                        updateLineColor(colorInactive);
                    }
                }
            });
        }
    }

    @Override
    public void onAnswerQuestion(boolean isAnswered) {
        super.onAnswerQuestion(isAnswered);
        if (!isAnswered) {
            updateLineColor(colorError);
            setFocus(getContext());
        } else {
            int color = hasFocus() ? colorActive : colorInactive;
            updateLineColor(color);
        }
    }

    public void updateLineColor(int color) {
        if (vLine != null) vLine.setBackgroundColor(color);
    }

    protected void setupChangeListener() {
        answerText.addTextChangedListener(new TextWatcher() {
            private String oldText = "";

            @Override
            public void afterTextChanged(Editable s) {
//                if (!s.toString().equals(oldText)) {
//                    Collect.getInstance().getActivityLogger()
//                            .logInstanceAction(this, "answerTextChanged", s.toString(),
//                                    getFormEntryPrompt().getIndex());
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    @Override
    public void clearAnswer() {
        answerText.setText(null);
    }

    public EditText getAnswerTextField() {
        return answerText;
    }

    @Override
    public IAnswerData getAnswer() {
        clearFocus();

        String s = getAnswerText();
        return !s.equals("") ? new StringData(s) : null;
    }

    @NonNull
    public String getAnswerText() {
        return answerText.getText().toString();
    }


    @Override
    public void setFocus(Context context) {
        // Put focus on text input field and display soft keyboard if appropriate.
        answerText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!readOnly) {
            inputManager.showSoftInput(answerText, 0);
            /*
             * If you do a multi-question screen after a "add another group" dialog, this won't
             * automatically pop up. It's an Android issue.
             *
             * That is, if I have an edit text in an activity, and pop a dialog, and in that
             * dialog's button's OnClick() I call edittext.requestFocus() and
             * showSoftInput(edittext, 0), showSoftinput() returns false. However, if the edittext
             * is focused before the dialog pops up, everything works fine. great.
             */
        } else {
            inputManager.hideSoftInputFromWindow(answerText.getWindowToken(), 0);
        }
    }

    @Override
    public boolean canGetFocus() {
        return !readOnly;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !event.isAltPressed() && super.onKeyDown(keyCode, event);
    }


    @Override
    public void setOnLongClickListener(View.OnLongClickListener l) {
        answerText.setOnLongClickListener(l);
    }


    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        answerText.cancelLongPress();
    }

}
