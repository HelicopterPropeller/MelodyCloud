package com.example.neteasecloudmusic.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.utils.Utils;

public class OuterRecyclerView extends RecyclerView {

    double x0;
    double y0;

    public OuterRecyclerView(@NonNull Context context) {
        super(context);
    }

    public OuterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OuterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = ev.getRawX();
                y0 = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                double x = ev.getRawX();
                double y = ev.getRawY();
                if (Utils.isVerticalSlide(x0, y0, x, y)) getParent().requestDisallowInterceptTouchEvent(true);
                else getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.onInterceptTouchEvent(ev);
    }
}
