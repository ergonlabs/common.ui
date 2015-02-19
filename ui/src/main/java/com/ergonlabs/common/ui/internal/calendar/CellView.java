package com.ergonlabs.common.ui.internal.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ergonlabs.common.ui.DateInfo;

/**
 * Created by stefanrusek on 6/10/14.
 */
public class CellView extends TextView implements DateInfo.Cell {

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

    public void setDate(long date, int dayOfMonth, DateInfo dateInfo) {
        this.date = date;

        setText(Integer.toString(dayOfMonth));

        setBackgroundResource(android.R.color.transparent);

        if (dateInfo != null) {
            setDateInfo(dateInfo);
        }
    }

    public void setDateInfo(final DateInfo dateInfo) {
        dateInfo.setBackground(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateInfo.onClick(CellView.this, date);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return dateInfo.onLongClick(CellView.this, date);
            }
        });
    }
}
