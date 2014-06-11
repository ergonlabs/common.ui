package com.ergonlabs.common.ui.internal.calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class MonthView extends LinearLayout {

    private final TextView title;
    private final WeekView week;
    private final NoScrollGridView days;

    public MonthView(Context context, long minDate, long maxDate, DateHelper dh, DateFormat df) {
        super(context);

        setOrientation(VERTICAL);

        title = new TextView(context);
        week = new WeekView(context, dh);
        days = new NoScrollGridView(context);

        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (title.getTextSize() * 1.5));
        title.setText(DateUtils.formatDateTime(context, minDate, DateUtils.FORMAT_UTC | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_DATE));

        days.setNumColumns(7);
        days.setAdapter(new CellViewAdapter(context, minDate, maxDate, dh, df));

        addView(title);
        addView(week);
        addView(days);
    }
}
