package com.ergonlabs.common.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ergonlabs.common.ui.internal.calendar.MonthView;

import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

public class CalendarView extends FrameLayout {

    CalendarAdapter adapter;
    int monthPadding;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        adapter = new CalendarAdapter(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0);

        try {
            setStartOfWeek(a.getInteger(R.styleable.CalendarView_startOfWeek, Calendar.SUNDAY));

            setDateRange(
                    a.getInteger(R.styleable.CalendarView_minDate, CalendarAdapter.DATE_THIS_MONTH),
                    a.getInteger(R.styleable.CalendarView_maxDate, CalendarAdapter.DATE_THIS_MONTH)
            );

            String tzName = a.getString(R.styleable.CalendarView_timeZone);
            if (tzName != null)
                setTimeZone(TimeZone.getTimeZone(tzName));
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
        removeAllViews();

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (monthPadding != 0) {
            int padding = getResources().getDimensionPixelSize(monthPadding);
            linearLayout.setPadding(padding, padding, padding, padding);
        }

        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            MonthView monthView = (MonthView) adapter.getMonth(i, linearLayout, false);
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

        addViewInLayout(linearLayout, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT), true);
    }

    public long getStartDate() {
        return adapter.getStartOfWeek();
    }

    public long getEndDate() {
        return adapter.getEndDate();
    }

    public long getMinDate() {
        return adapter.getMinDate();
    }

    public long getMaxDate() {
        return adapter.getMaxDate();
    }

    public void setDateRange(long minDate, long maxDate) {
        adapter.setDateRange(minDate, maxDate);
    }

    public TimeZone getTimeZone() {
        return adapter.getTimeZone();
    }

    public void setTimeZone(TimeZone timeZone) {
        adapter.setTimeZone(timeZone);
    }

    public int getStartOfWeek() {
        return adapter.getStartOfWeek();
    }

    public void setStartOfWeek(int startOfWeek) {
        adapter.setStartOfWeek(startOfWeek);
    }

    public void setDateInfo(Collection<DateInfo> infos) {
        adapter.setDateInfo(infos);
    }

    public void setDateInfo(Collection<DateInfo> infos, DateInfo defaultMonthDays, DateInfo nonMonthDays) {
        adapter.setDateInfo(infos, defaultMonthDays, nonMonthDays);
    }

    public int getMonthBackgroundResource() {
        return adapter.getMonthBackgroundResource();
    }

    public void setMonthBackgroundResource(int monthBackgroundRes, int monthPaddingDimenRes) {
        this.monthPadding = monthPaddingDimenRes;
        adapter.setMonthBackgroundResource(monthBackgroundRes);
    }
}
