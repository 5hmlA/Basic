package com.baselib.helper;

import android.graphics.Paint;

public class DrawHelper {
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -fontMetrics.top - fontMetrics.bottom;
    }
}
