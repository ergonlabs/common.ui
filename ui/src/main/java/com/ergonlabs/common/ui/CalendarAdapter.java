package com.ergonlabs.common.ui;

import android.content.Context;
import android.database.Observable;
import android.view.View;
import android.view.ViewGroup;

import com.ergonlabs.common.ui.internal.calendar.DateFormat;
import com.ergonlabs.common.ui.internal.calendar.DateHelper;
import com.ergonlabs.common.ui.internal.calendar.MonthView;

import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by stefanrusek on 11/26/14.
 */
public class CalendarAdapter extends Observable<CalendarAdapter.OnDataChanged> {
    public static final int DATE_TODAY = DateHelper.DATE_TODAY;
    public static final int DATE_YESTERDAY = DateHelper.DATE_YESTERDAY;
    public static final int DATE_TOMORROW = DateHelper.DATE_TOMORROW;
    public static final int DATE_THIS_MONTH = DateHelper.DATE_THIS_MONTH;
    public static final int DATE_ONE_MONTH = DateHelper.DATE_ONE_MONTH;
    public static final int DATE_TWO_MONTHS = DateHelper.DATE_TWO_MONTHS;
    public static final int DATE_THREE_MONTHS = DateHelper.DATE_THREE_MONTHS;
    public static final int DATE_FOUR_MOUNTS = DateHelper.DATE_FOUR_MOUNTS;
    public static final int DATE_ONE_MONTH_AGO = DateHelper.DATE_ONE_MONTH_AGO;
    public static final int DATE_TWO_MONTHS_AGO = DateHelper.DATE_TWO_MONTHS_AGO;
    public static final int DATE_THREE_MONTHS_AGO = DateHelper.DATE_THREE_MONTHS_AGO;
    public static final int DATE_FOUR_MONTHS_AGO = DateHelper.DATE_FOUR_MONTHS_AGO;

    private final Context context;

    long startDate;
    long endDate;
    long minDate;
    long maxDate;

    int monthBackground = 0;

    boolean needsNewAdapter = true;

    public interface OnDataChanged {
        void dataChanged();
    }

    DateHelper dh = new DateHelper();
    DateFormat df = new DateFormat(null, null, null, dh);
    List<DateHelper.MonthInfo> months;

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    private List<DateHelper.MonthInfo> getMonths() {
        if (months == null || needsNewAdapter) {
            months = dh.getMonths(minDate, maxDate);
            needsNewAdapter = false;
        }
        return months;
    }

    public int getCount() {
        return getMonths().size();
    }

    public View getMonth(int i, ViewGroup parent, boolean addToParent) {
        DateHelper.MonthInfo info = getMonths().get(i);
        MonthView monthView = new MonthView(context, info.minDate, info.maxDate, dh, df);
        if (monthBackground != 0)
            monthView.setBackgroundResource(monthBackground);
        if (addToParent)
            parent.addView(monthView);
        return monthView;
    }

    public long getStartDate() {
        return dh.getStartOfWeek();
    }

    public long getEndDate() {
        return endDate;
    }

    public long getMinDate() {
        return minDate;
    }

    public long getMaxDate() {
        return maxDate;
    }

    public void setDateRange(long minDate, long maxDate) {
        this.minDate = dh.normalize(minDate, DateHelper.MODE_MIN);
        this.maxDate = dh.normalize(maxDate, DateHelper.MODE_MAX, dh.getLastSize());
        refresh();
    }

    private void refresh() {
        this.startDate = dh.normalize(this.minDate, DateHelper.MODE_MIN, DateHelper.SIZE_MONTH, DateHelper.PAD_START_OF_WEEK);
        this.endDate = dh.normalize(this.maxDate, DateHelper.MODE_MAX, DateHelper.SIZE_MONTH, DateHelper.PAD_END_OF_WEEK);
        if (!needsNewAdapter) {
            this.needsNewAdapter = true;
            synchronized (this) {
                for (OnDataChanged callback : mObservers)
                    callback.dataChanged();
            }
        }
    }

    public TimeZone getTimeZone() {
        return dh.getTimeZone();
    }

    public void setTimeZone(TimeZone timeZone) {
        dh.setTimeZone(timeZone);
        refresh();
    }

    public int getStartOfWeek() {
        return dh.getStartOfWeek();
    }

    public void setStartOfWeek(int startOfWeek) {
        dh.setStartOfWeek(startOfWeek);
        refresh();
    }

    public void setDateInfo(Collection<DateInfo> infos) {
        setDateInfo(infos, null, null);
    }

    public void setDateInfo(Collection<DateInfo> infos, DateInfo defaultMonthDays, DateInfo nonMonthDays) {
        df = new DateFormat(infos, defaultMonthDays, nonMonthDays, dh);
        refresh();
    }

    public int getMonthBackgroundResource() {
        return monthBackground;
    }

    public void setMonthBackgroundResource(int monthBackgroundRes) {
        this.monthBackground = monthBackgroundRes;
        refresh();
    }
}
