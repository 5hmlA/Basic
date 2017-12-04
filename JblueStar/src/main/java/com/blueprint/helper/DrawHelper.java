package com.blueprint.helper;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;

import static com.blueprint.LibApp.getContext;

public class DrawHelper {

    /**
     * 计算反色
     * @param color
     * @return
     */
    public static int getConverserColor(int color){
        return Color.rgb(255-Color.red(color), 255-Color.green(color), 255-Color.blue(color));
    }

    public static float getFontHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -fontMetrics.top-fontMetrics.bottom;
    }

    public static void drawTextCenterinRectf(Canvas canvas, RectF rectF, Paint textPaint, String msg){
        canvas.drawText(msg, rectF.centerX(), rectF.centerY()+getFontHeight(textPaint)/2f, textPaint);
    }

    /**
     * [checked,pressed],normal
     *
     * @param resIds
     * @return
     */
    public static StateListDrawable getListDrable(@Size(value = 2) @DrawableRes int... resIds){
        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable
                .addState(new int[]{android.R.attr.state_checked}, ContextCompat.getDrawable(getContext(), resIds[0]));
        listDrawable
                .addState(new int[]{android.R.attr.state_pressed}, ContextCompat.getDrawable(getContext(), resIds[0]));
        listDrawable.addState(new int[]{}, ContextCompat.getDrawable(getContext(), resIds[1]));
        return listDrawable;
    }

    public static ColorStateList getColorStateList(@Size(value = 2) @ColorInt int... colors){
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * checked,pressed,normal
     *
     * @param colors
     * @return
     */
    public static ColorStateList getColorStateList2(@Size(value = 3) @ColorInt int... colors){

        int[][] states = new int[][]{new int[]{android.R.attr.state_checked},// unchecked
                new int[]{android.R.attr.state_pressed},//pressed
                new int[]{}   //normal
        };
        return new ColorStateList(states, colors);
    }

}
