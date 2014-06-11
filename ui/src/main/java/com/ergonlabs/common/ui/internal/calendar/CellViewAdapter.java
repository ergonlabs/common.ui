package com.ergonlabs.common.ui.internal.calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ergonlabs.common.ui.DateInfo;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class CellViewAdapter extends BaseAdapter {
    private final Context context;
    private final long count;
    private final long startDate;
    private final long startOfMonth;
    private final long endOfMonth;
    private final long minDate;
    private final long maxDate;
    private final DateHelper dh;
    private final DateFormat df;

    public CellViewAdapter(Context context, long minDate, long maxDate, DateHelper dh, DateFormat df) {
        this.context = context;
        this.dh = dh;
        this.df = df;

        long startDate = dh.normalize(minDate, DateHelper.MODE_MIN, DateHelper.SIZE_MONTH, DateHelper.PAD_START_OF_WEEK);
        long endDate = dh.normalize(maxDate, DateHelper.MODE_MAX, DateHelper.SIZE_MONTH, DateHelper.PAD_END_OF_WEEK);

        startOfMonth = dh.normalize(minDate, DateHelper.MODE_MIN, DateHelper.SIZE_MONTH);
        endOfMonth = dh.normalize(maxDate, DateHelper.MODE_MAX, DateHelper.SIZE_MONTH);

        count = (endDate - startDate) / DateUtils.DAY_IN_MILLIS + 1;
        this.startDate = startDate;
        this.minDate = minDate;
        this.maxDate = maxDate;
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
        return dh.normalize(startDate + position * DateUtils.DAY_IN_MILLIS, DateHelper.MODE_MIN);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    boolean inMonth(long date) {
        return date >= startOfMonth && date < endOfMonth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CellView cv = convertView != null ? (CellView) convertView : new CellView(context);
        long date = getDate(position);

        DateInfo info = inMonth(date) ? df.get(date) : df.getNonMonthDays();

        cv.setDate(date, dh.getDayOfMonth(date), info);
        return cv;
    }
}
