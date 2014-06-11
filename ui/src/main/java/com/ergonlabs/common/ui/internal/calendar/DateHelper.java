package com.ergonlabs.common.ui.internal.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class DateHelper {
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

    public static final int MODE_MIN = 0;
    public static final int MODE_MAX = 1;

    public static final int SIZE_DAY = 0;
    public static final int SIZE_MONTH = 1;

    public static final int PAD_NONE = 0;
    public static final int PAD_START_OF_WEEK = 1;
    public static final int PAD_END_OF_WEEK = 2;

    private TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private int startOfWeek = Calendar.SUNDAY;
    private int lastSize;

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public int getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(int startOfWeek) {
        this.startOfWeek = startOfWeek;
    }

    public int getLastSize() {
        return lastSize;
    }

    public long normalize(long in, int mode) {
        return normalize(in, mode, SIZE_DAY);
    }

    public long normalize(long in, int mode, int size) {
        return normalize(in, mode, size, PAD_NONE);
    }

    public long normalize(long in, int mode, int size, int padding) {
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

    public List<MonthInfo> getMonths(long minDate, long maxDate) {
        List<MonthInfo> months = new ArrayList<MonthInfo>();

        Calendar min = Calendar.getInstance(timeZone);
        min.setTimeInMillis(minDate);

        Calendar max = Calendar.getInstance(timeZone);
        max.setTimeInMillis(minDate);

        int startMonth = getMonth(minDate);
        int endMonth = getMonth(maxDate);

        for (int month = startMonth; month <= endMonth; month++) {
            long newMin = Math.max(minDate, normalize(min.getTimeInMillis(), MODE_MIN, SIZE_MONTH));
            long newMax = Math.min(maxDate, normalize(max.getTimeInMillis(), MODE_MAX, SIZE_MONTH));

            months.add(new MonthInfo(newMin, newMax));

            min.add(Calendar.MONTH, 1);
            max.add(Calendar.MONTH, 1);
        }

        return months;
    }

    private int getMonth(long date) {
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(date);
        return c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH);
    }

    public int getDayOfMonth(long date) {
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static class MonthInfo {
        public final long minDate;
        public final long maxDate;

        public MonthInfo(long minDate, long maxDate) {
            this.minDate = minDate;
            this.maxDate = maxDate;
        }
    }
}
