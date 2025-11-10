package com.example.neteasecloudmusic.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class InnerRecyclerView extends RecyclerView {

    private float startX, startY;
    private final int touchSlop;

    public InnerRecyclerView(Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public InnerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public InnerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        ViewGroup v = this;
        while (v != null && !(v.getParent() instanceof ViewPager2)) {
            v = (ViewGroup) v.getParent();
        }

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startX = e.getX();
                startY = e.getY();
                v.requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = e.getX() - startX;
                float dy = e.getY() - startY;

                if (Math.abs(dx) < touchSlop && Math.abs(dy) < touchSlop) break;

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        if (!canScrollHorizontally(-1)) {
                            v.requestDisallowInterceptTouchEvent(false);
                        } else {
                            v.requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        if (!canScrollHorizontally(1)) {
                            v.requestDisallowInterceptTouchEvent(false);
                        } else {
                            v.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                } else {
                    v.requestDisallowInterceptTouchEvent(false);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.onInterceptTouchEvent(e);
    }
}

