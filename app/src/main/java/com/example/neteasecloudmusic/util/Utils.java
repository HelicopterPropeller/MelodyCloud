package com.example.neteasecloudmusic.util;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    public static int getMainColor(Bitmap bitmap) {
        AtomicInteger color = new AtomicInteger(-1);
        Palette.from(bitmap).generate(palette -> {

            Palette.Swatch swatch = palette.getDominantSwatch();
            if (swatch != null) {
                color.set(swatch.getRgb());
            }
        });
        return color.get();
    }

}
