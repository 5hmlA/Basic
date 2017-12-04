package com.blueprint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.SeekBar;

import com.blueprint.helper.DrawHelper;

/**
 * @another 江祖赟
 * @date 2017/11/15 0015.
 */
@SuppressLint("AppCompatCustomView")
public class JSeekBar extends SeekBar {

    Paint mPaint;
    Paint mTextPaint;
    public String suffix = "个方块";
    private float offSet = dp2px(5);
    private float conorsRadius = dp2px(3);
    private RectF mTipRect = new RectF();
    private float mFontHeight;
    private static final int[] ATTRS = new int[]{android.R.attr.textColor, android.R.attr.shadowColor};
    private int mW;

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(Color.WHITE);
                setTextAlign(Align.CENTER);
                setTextSize(dp2px(12));
            }
        };
    }

    public JSeekBar(Context context){
        super(context);
    }

    public JSeekBar(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mTextPaint.setColor(a.getColor(0, Color.WHITE));
        mPaint.setColor(a.getColor(1, Color.RED));
        a.recycle();

    }

    public JSeekBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        mW = w;
        super.onSizeChanged(w, h, oldw, oldh);
        suffix = "%d"+suffix;
        mFontHeight = DrawHelper.getFontHeight(mTextPaint);
        float textWidth = mTextPaint.measureText(suffix);
        mTipRect.set(0, -offSet-mFontHeight*2, textWidth+mFontHeight*3f/2, -offSet);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Drawable thumb = getThumb();
        if(thumb != null) {
            int save = canvas.save();
            Rect thumbBounds = thumb.getBounds();
            canvas.translate(calcureCanvasOffset(thumbBounds), 0);
            canvas.drawRoundRect(mTipRect, conorsRadius, conorsRadius, mPaint);
            canvas.drawText(getProgressMsg(), mTipRect.centerX(), mTipRect.centerY()+mFontHeight/2f, mTextPaint);
            canvas.restoreToCount(save);
        }
    }

    private float calcureCanvasOffset(Rect thumbBounds){
        float offset = thumbBounds.centerX()-mTipRect.centerX();
        float right = mW-mTipRect.width()/2;
        offset = offset<0 ? 0 : offset>right ? right : offset;
        return offset;
    }

    private String getProgressMsg(){
        return String.format(suffix, getProgress());
    }

    public static float dp2px(float px){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
    }

    public JSeekBar setTipBgColor(@ColorInt int tipBgColor){
        mPaint.setColor(tipBgColor);
        return this;
    }

    public JSeekBar setTipTextColor(@ColorInt int tipTextColor){
        mTextPaint.setColor(tipTextColor);
        return this;
    }
}
