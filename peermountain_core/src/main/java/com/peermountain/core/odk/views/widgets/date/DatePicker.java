package com.peermountain.core.odk.views.widgets.date;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.peermountain.core.R;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.javarosa.core.model.data.DateData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.joda.time.LocalDateTime;

import java.util.Date;

/**
 * Created by Galeen on 2/26/18.
 */

public class DatePicker extends QuestionWidget {
    MaterialCalendarView materialCalendarView;

    public DatePicker(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
        createCalendar();
    }

    private void createCalendar() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_date_widget, viewParent);
            materialCalendarView = viewParent.findViewById(R.id.calendarView);
            if (getFormEntryPrompt().getAnswerValue() != null) {
                LocalDateTime answerDate = new LocalDateTime(getFormEntryPrompt().getAnswerValue().getValue());
                materialCalendarView.setSelectedDate(answerDate.toDate());
            } else {
                materialCalendarView.setSelectedDate(new Date());
            }

            materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                }
            });
        }
    }

    @Override
    public IAnswerData getAnswer() {
        Date date = materialCalendarView.getSelectedDate() != null ? materialCalendarView.getSelectedDate().getDate() : new Date();
        return new DateData(date);
    }

    @Override
    public void clearAnswer() {
        materialCalendarView.setSelectedDate(new Date());
    }

    @Override
    public void setFocus(Context context) {

    }

    @Override
    public boolean canGetFocus() {
        return false;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {

    }
}
