package com.peermountain.core.odk.views.widgets.range;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.appyvet.materialrangebar.RangeBar;
import com.peermountain.core.R;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;
import com.peermountain.common.utils.PmDialogUtils;

import org.javarosa.core.model.RangeQuestion;
import org.javarosa.form.api.FormEntryPrompt;

import java.math.BigDecimal;

/**
 * Created by Galeen on 2/13/2018.
 */

public abstract class RangeWidget extends QuestionWidget {
    private static final String VERTICAL_APPEARANCE = "vertical";
    private static final String NO_TICKS_APPEARANCE = "no-ticks";
    private static final String PICKER_APPEARANCE = "picker";
    private static final String NO_STYLE = "NO_STYLE";

    protected BigDecimal rangeStart;
    protected BigDecimal rangeEnd;
    protected BigDecimal rangeStep;
    protected BigDecimal actualValue;

    private RangeBar slider;
    private boolean isPickerAppearance, isNoTicks, isVertical;

    public RangeWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
        setUpWidgetParameters();
        setUpAppearance();

        if (prompt.isReadOnly() && slider != null) {
            slider.setEnabled(false);
        }

    }

    private void setUpWidgetParameters() {
        RangeQuestion rangeQuestion = (RangeQuestion) getFormEntryPrompt().getQuestion();

        rangeStart = rangeQuestion.getRangeStart();
        rangeEnd = rangeQuestion.getRangeEnd();
        rangeStep = rangeQuestion.getRangeStep().abs();
    }

    private void setUpAppearance() {
        String appearance = getFormEntryPrompt().getQuestion().getAppearanceAttr();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_slider, viewParent);
            slider = viewParent.findViewById(R.id.pmSlider);
            tvHelp.setVisibility(VISIBLE);

            if (appearance != null) {
                isPickerAppearance = appearance.contains(PICKER_APPEARANCE);
                isVertical = appearance.contains(VERTICAL_APPEARANCE);
                isNoTicks = appearance.contains(NO_TICKS_APPEARANCE);
            }
            setUpLayoutElements();
        }
    }

    private void setUpLayoutElements() {
        if (isWidgetValid()) {
            if (getFormEntryPrompt().getAnswerValue() != null) {
                actualValue = new BigDecimal(getFormEntryPrompt().getAnswerValue().getValue().toString());
                if (actualValue.compareTo(rangeStart) < 0) {
                    actualValue = rangeStart;
                } else if (actualValue.compareTo(rangeEnd) >= 0) {
                    actualValue = rangeEnd;
                }
            } else {
                setUpNullValue();
            }

//            if (!isPickerAppearance) {
            setUpSeekBar();
            setUpActualValueLabel();
//            } else {
//                setUpDisplayedValuesForNumberPicker();
//                answerTextView.setText(getFormEntryPrompt().getAnswerValue() != null ? String.valueOf(actualValue) : String.valueOf(0));
//            }
        }
    }

    private boolean isWidgetValid() {
        boolean result = true;
        if (rangeStep.compareTo(BigDecimal.ZERO) == 0
                || rangeEnd.subtract(rangeStart).remainder(rangeStep).compareTo(BigDecimal.ZERO) != 0) {
            disableWidget();
            result = false;
        }
        return result;
    }

    private void disableWidget() {
        PmDialogUtils.showErrorToast(getContext(), "invalid range data for : " + getFormEntryPrompt().getShortText());
        slider.setEnabled(false);
//        tvCurrentValue.setText("no value");
    }

    private void setUpNullValue() {
        actualValue = rangeStart;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpSeekBar() {
        slider.setTickEnd(rangeEnd.floatValue());
        slider.setTickStart(rangeStart.floatValue());
        slider.setTickInterval(rangeStep.floatValue());
        slider.setSeekPinByValue(actualValue.floatValue());

        slider.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                if (currentColor == colorError) {
                    currentColor = colorActive;
                    initSliderColor();
                    onAnswerQuestion(true);
                }
                actualValue = new BigDecimal(rightPinValue);
                setUpActualValueLabel();
            }
        });

        slider.setOnTouchListener(new SeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                seekBar.getThumb().mutate().setAlpha(255);
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        blockParentTouch(false);
                        setFocus(getContext());
                        break;
                    case MotionEvent.ACTION_UP:
                        blockParentTouch(true);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    int currentColor;

    @Override
    public void onAnswerQuestion(boolean isAnswered) {
        super.onAnswerQuestion(isAnswered);
        if (slider == null) return;
        if (!isAnswered) {
            currentColor = colorError;
        } else {
            currentColor = colorActive;
            setUpActualValueLabel();
        }
        initSliderColor();
    }

    public void initSliderColor() {
        slider.setConnectingLineColor(currentColor);
        slider.setSelectorColor(currentColor);
    }


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

    @Override
    public void clearAnswer() {
        setUpNullValue();
    }

    protected abstract void setUpActualValueLabel();

//    protected abstract void setUpDisplayedValuesForNumberPicker();
}
