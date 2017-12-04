package com.blueprint.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import com.blueprint.LibApp;

/**
 * @author yun.
 * @date 2016/12/21
 * @des [居中的 ImageSpan]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JVerticalImageSpan extends ImageSpan {
    public JVerticalImageSpan(int resourceId){
        super(LibApp.getContext(), resourceId);
    }

    public JVerticalImageSpan(Drawable drawable){
        super(drawable);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt){
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if(fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom-fmPaint.top;//字体高
            int drHeight = rect.bottom-rect.top;//图片高
            int top = drHeight/2-fontHeight/4;
            int bottom = drHeight/2+fontHeight/4;
            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint){
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ( ( bottom-top )-drawable.getBounds().bottom )/2+top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}