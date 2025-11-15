package com.example.neteasecloudmusic.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class OuterRecyclerView extends RecyclerView {

    private double x0, y0;
    private final int touchSlop;

    public OuterRecyclerView(@NonNull Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public OuterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public OuterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = ev.getX();
                y0 = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                double dx = ev.getX() - x0;
                double dy = ev.getY() - y0;

                if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) break;

                if (Math.abs(dy) > Math.abs(dx)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
