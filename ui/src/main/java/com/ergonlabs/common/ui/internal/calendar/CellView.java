package com.ergonlabs.common.ui.internal.calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by stefanrusek on 6/10/14.
 */
public class CellView extends TextView {

    private long date;

    public CellView(Context context) {
        super(context);
        init();
    }


    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setDate(long date) {
        this.date = date;

        String s = DateUtils.formatDateTime(getContext(), date, DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE);
        setText(s);
    }
}
