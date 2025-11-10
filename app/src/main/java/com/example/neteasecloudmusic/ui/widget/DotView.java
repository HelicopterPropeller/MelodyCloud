package com.example.neteasecloudmusic.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.neteasecloudmusic.R;

import java.util.ArrayList;
import java.util.List;

public class DotView extends LinearLayout {
    private List<View> dots = new ArrayList<>();

    public DotView(Context context) {
        super(context);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        dots.add(findViewById(R.id.left_dot));
        dots.add(findViewById(R.id.left_second_dot));
        dots.add(findViewById(R.id.left_mid_dot));
        dots.add(findViewById(R.id.right_mid_dot));
        dots.add(findViewById(R.id.right_second_dot));
        dots.add(findViewById(R.id.right_dot));
    }

    public void attachToViewPager(ViewPager2 pager) {
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }
}
