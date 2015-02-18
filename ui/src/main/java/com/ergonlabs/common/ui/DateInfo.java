package com.ergonlabs.common.ui;

import android.view.View;

import com.ergonlabs.common.ui.internal.calendar.CellView;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class DateInfo {
    public long date;
    public Integer backgroundRes;

    public DateInfo(long date, Integer backgroundRes) {
        this(date);
        this.backgroundRes = backgroundRes;
    }

    public DateInfo(int backgroundRes) {
        this();
        this.backgroundRes = backgroundRes;
    }

    protected DateInfo(long date) {
        this();
        this.date = date;
    }

    protected DateInfo() {

    }

    public void setBackground(View cell) {
        cell.setBackgroundResource(backgroundRes);
    }

    public interface Cell {
        public void setDateInfo(DateInfo dateInfo);
    }

    public void onClick(Cell cell, long date) {
    }

    public boolean onLongClick(Cell cell, long date) {
        return false;
    }
}
