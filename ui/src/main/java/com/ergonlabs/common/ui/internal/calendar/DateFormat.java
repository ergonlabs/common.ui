package com.ergonlabs.common.ui.internal.calendar;

import com.ergonlabs.common.ui.DateInfo;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class DateFormat {

    final HashMap<Long, DateInfo> dateInfo;
    final DateInfo defaultMonthDays;
    final DateInfo nonMonthDays;

    public DateFormat(Collection<DateInfo> srcInfo, DateInfo defaultMonthDays, DateInfo nonMonthDays, DateHelper dh) {
        this.defaultMonthDays = defaultMonthDays != null ? defaultMonthDays : new DateInfo(android.R.color.white);
        this.nonMonthDays = nonMonthDays != null ? nonMonthDays : new DateInfo(android.R.color.darker_gray);

        dateInfo = new HashMap<>();
        if (srcInfo != null)
            for (DateInfo info: srcInfo)
                dateInfo.put(dh.normalize(info.date, DateHelper.MODE_MIN), info);
    }

    public DateInfo get(long date) {
        return or(dateInfo.get(date), defaultMonthDays);
    }

    private DateInfo or(DateInfo a, DateInfo b) {
        return a != null ? a : b;
    }

    public DateInfo getNonMonthDays() {
        return nonMonthDays;
    }
}
