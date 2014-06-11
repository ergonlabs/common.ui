package com.ergonlabs.common.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ergonlabs.common.ui.internal.calendar.DateFormat;
import com.ergonlabs.common.ui.internal.calendar.DateHelper;
import com.ergonlabs.common.ui.internal.calendar.MonthView;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

public class CalendarView extends FrameLayout {
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

    long startDate;
    long endDate;
    long minDate;
    long maxDate;

    int monthBackground = 0;
    int monthPadding = 0;

    boolean needsNewAdapter = true;

    DateHelper dh = new DateHelper();
    DateFormat df = new DateFormat(null, null, null, dh);


    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0);

        try {
            int startOfWeek = a.getInteger(R.styleable.CalendarView_startOfWeek, Calendar.SUNDAY);
            dh.setStartOfWeek(Math.min(Calendar.SATURDAY, Math.max(Calendar.SUNDAY, startOfWeek)));

            setDateRange(
                    a.getInteger(R.styleable.CalendarView_minDate, DATE_THIS_MONTH),
                    a.getInteger(R.styleable.CalendarView_maxDate, DATE_THIS_MONTH)
            );

            String tzName = a.getString(R.styleable.CalendarView_timeZone);
            if (tzName != null)
                dh.setTimeZone(TimeZone.getTimeZone(tzName));
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureSetup();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ensureSetup();
        super.onLayout(changed, left, top, right, bottom);
    }

    private void ensureSetup() {
        if (!needsNewAdapter)
            return;

        removeAllViews();

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (monthPadding != 0) {
            int padding = getResources().getDimensionPixelSize(monthPadding);
            linearLayout.setPadding(padding, padding, padding, padding);
        }

        List<DateHelper.MonthInfo> months = dh.getMonths(minDate, maxDate);
        for (DateHelper.MonthInfo info : months) {
            MonthView monthView = new MonthView(getContext(), info.minDate, info.maxDate, dh, df);
            if (monthBackground != 0)
                monthView.setBackgroundResource(monthBackground);
            if (monthPadding != 0) {
                int padding = getResources().getDimensionPixelSize(monthPadding);
                monthView.setPadding(padding, padding, padding, padding);
                if (linearLayout.getChildCount() > 0) {
                    View view = new View(getContext());
                    view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, padding));
                    linearLayout.addView(view);
                }

            }
            linearLayout.addView(monthView);
        }

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout);
        addViewInLayout(scrollView, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT), true);

        needsNewAdapter = false;
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
            requestLayout();
        }
    }

    public TimeZone getTimeZone() {
        return dh.getTimeZone();
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

    public void setMonthBackgroundResource(int monthBackgroundRes, int monthPaddingDimenRes) {
        this.monthBackground = monthBackgroundRes;
        this.monthPadding = monthPaddingDimenRes;
        refresh();
    }
}
