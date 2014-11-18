package com.ergonlabs.common.ui;

/**
 * Created by stefanrusek on 6/11/14.
 */
public class DateInfo {
    public long date;
    public Integer backgroundRes;

    public DateInfo(long date, Integer backgroundRes) {
        this.date = date;
        this.backgroundRes = backgroundRes;
    }

    public DateInfo(int backgroundRes) {
        this.backgroundRes = backgroundRes;
    }
}
