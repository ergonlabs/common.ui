package com.ergonlabs.common.ui.internal.calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class WeekView extends LinearLayout {
    public WeekView(Context context, DateHelper dh) {
        super(context);

        setOrientation(HORIZONTAL);

        int startOfWeek = dh.getStartOfWeek();
        for (int i = 0; i < 7; i++) {
            String day = DateUtils.getDayOfWeekString(1 + (startOfWeek - 1 + i) % 7, DateUtils.LENGTH_SHORTER);
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(day);
            addView(tv, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }
    }
}
