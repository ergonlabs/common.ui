package com.ergonlabs.common.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ergonlabs.common.ui.internal.calendar.CellView;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarView extends FrameLayout {
    public static final int DATE_TODAY = 1;
    public static final int DATE_YESTERDAY = 2;
    public static final int DATE_TOMORROW = 3;
    public static final int DATE_THIS_MONTH = 4;
    public static final int DATE_ONE_MONTH = 10;
    public static final int DATE_TWO_MONTHS = 11;
    public static final int DATE_THREE_MONTHS = 12;
    public static final int DATE_FOUR_MOUNTS = 13;
    public static final int DATE_ONE_MONTH_AGO = 20;
    public static final int DATE_TWO_MONTHS_AGO = 21;
    public static final int DATE_THREE_MONTHS_AGO = 22;
    public static final int DATE_FOUR_MONTHS_AGO = 23;
    private static final int MODE_MIN = 0;
    private static final int MODE_MAX = 1;
    private static final int SIZE_DAY = 0;
    private static final int SIZE_MONTH = 1;
    private static final int PAD_NONE = 0;
    private static final int PAD_START_OF_WEEK = 1;
    private static final int PAD_END_OF_WEEK = 2;

    long startDate;
    long endDate;
    long minDate;
    long maxDate;
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    int startOfWeek;

    boolean needsNewAdapter = true;
    private int lastSize;

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
            startOfWeek = a.getInteger(R.styleable.CalendarView_startOfWeek, Calendar.SUNDAY);
            startOfWeek = Math.min(Calendar.SATURDAY, Math.max(Calendar.SUNDAY, startOfWeek));

            setDateRange(
                    a.getInteger(R.styleable.CalendarView_minDate, DATE_THIS_MONTH),
                    a.getInteger(R.styleable.CalendarView_maxDate, DATE_THIS_MONTH)
            );

            timeZone = TimeZone.getTimeZone(or(a.getString(R.styleable.CalendarView_timeZone), "UTC"));
        } finally {
            a.recycle();
        }
    }

    private static <T> T or(T a, T b) {
        if (a != null)
            return a;
        return b;
    }

    private long normalize(long in, int mode) {
        return normalize(in, mode, SIZE_DAY);
    }

    private long normalize(long in, int mode, int size) {
        return normalize(in, mode, size, PAD_NONE);
    }

    private long normalize(long in, int mode, int size, int padding) {
        lastSize = size;

        if (in > 0 && in < 100) {
            Calendar c = Calendar.getInstance(getTimeZone());
            switch ((int) in) {
                case DATE_THIS_MONTH:
                    size = SIZE_MONTH;
                case DATE_TODAY:
                    break;
                case DATE_YESTERDAY:
                    c.add(Calendar.DATE, -1);
                    break;
                case DATE_TOMORROW:
                    c.add(Calendar.DATE, 1);
                    break;
                case DATE_FOUR_MOUNTS:
                    c.add(Calendar.MONTH, 1);
                case DATE_THREE_MONTHS:
                    c.add(Calendar.MONTH, 1);
                case DATE_TWO_MONTHS:
                    c.add(Calendar.MONTH, 1);
                case DATE_ONE_MONTH:
                    c.add(Calendar.MONTH, 1);
                    break;
                case DATE_FOUR_MONTHS_AGO:
                    c.add(Calendar.MONTH, -1);
                case DATE_THREE_MONTHS_AGO:
                    c.add(Calendar.MONTH, -1);
                case DATE_TWO_MONTHS_AGO:
                    c.add(Calendar.MONTH, -1);
                case DATE_ONE_MONTH_AGO:
                    c.add(Calendar.MONTH, -1);
                    break;

                default:
                    throw new IllegalArgumentException();
            }
            in = c.getTimeInMillis();
        }

        Calendar c = Calendar.getInstance(getTimeZone());
        c.setTimeInMillis(in);
        c.clear(Calendar.HOUR_OF_DAY);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        if (mode == MODE_MIN) {
            if (size == SIZE_MONTH)
                c.set(Calendar.DAY_OF_MONTH, 1);
        } else if (mode == MODE_MAX) {
            if (size == SIZE_MONTH) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, 1);
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        }

        if (padding == PAD_START_OF_WEEK)
            while (c.get(Calendar.DAY_OF_WEEK) != getStartOfWeek())
                c.add(Calendar.DATE, -1);

        if (padding == PAD_END_OF_WEEK) {
            int endOfWeek = 1 + ((getStartOfWeek() + 5) % 7);
            while (c.get(Calendar.DAY_OF_WEEK) != endOfWeek)
                c.add(Calendar.DATE, 1);
        }

        if (mode == MODE_MAX)
            // always end up 1 ms ahead of MODE_MIN
            c.add(Calendar.MILLISECOND, 1);

        return c.getTimeInMillis();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ensureSetup();
        super.onLayout(changed, left, top, right, bottom);
    }

    private void ensureSetup() {
        if (getChildCount() == 1 && !needsNewAdapter)
            return;

        if (getChildCount() == 0) {
            View child = new GridView(getContext());
            child.setId(R.id.grid_view);
            addView(child, -1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        GridView grid = (GridView) findViewById(R.id.grid_view);
        grid.setNumColumns(7);
        grid.setAdapter(new Adapter());

        invalidate();
        needsNewAdapter = false;
    }

    public long getStartDate() {
        return startDate;
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
        this.minDate = normalize(minDate, MODE_MIN);
        this.maxDate = normalize(maxDate, MODE_MAX, lastSize);
        refresh();
    }

    private void refresh() {
        this.startDate = normalize(this.minDate, MODE_MIN, SIZE_MONTH, PAD_START_OF_WEEK);
        this.endDate = normalize(this.maxDate, MODE_MAX, SIZE_MONTH, PAD_END_OF_WEEK);
        if (!needsNewAdapter) {
            this.needsNewAdapter = true;
            requestLayout();
        }
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public int getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(int startOfWeek) {
        this.startOfWeek = startOfWeek;
        refresh();
    }

    private class Adapter extends BaseAdapter {

        private final long count;

        public Adapter() {
            count = (getEndDate() - getStartDate()) / DateUtils.DAY_IN_MILLIS + 1;
        }

        @Override
        public int getCount() {
            return (int) count;
        }

        @Override
        public Long getItem(int position) {
            return getDate(position);
        }

        private long getDate(int position) {
            return getStartDate() + position * DateUtils.DAY_IN_MILLIS;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            long date = getDate(position);
            return date >= getMinDate() && date < getMaxDate();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CellView cv = convertView != null ? (CellView) convertView : new CellView(getContext());
            cv.setDate(getDate(position));
            return cv;
        }
    }
}
