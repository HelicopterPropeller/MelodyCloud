package com.example.neteasecloudmusic.utils;

import android.content.Context;

public class Utils {
    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static double getAngle(double x0, double y0, double x, double y) {
        double deltaX = x - x0;
        double deltaY = y - y0;
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    public static boolean isVerticalSlide(double x0, double y0, double x, double y) {
        double angle = getAngle(x0, y0, x, y);
        double absAngle = Math.abs(angle);
        return absAngle > 45 && absAngle < 135;
    }

    public static boolean isRightSlide(double x0, double y0, double x, double y) {
        double angle = getAngle(x0, y0, x, y);
        return angle > 135 && angle < -135;
    }
}
