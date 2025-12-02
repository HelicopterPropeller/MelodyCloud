package com.example.neteasecloudmusic.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    public static String formatTime(long millis) {
        int totalSeconds = (int) (millis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
