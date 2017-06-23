package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @another 江祖赟
 * @date 2017/6/22.
 */
public class JProgressView extends View {

    float mRingWidth = 8;

    RectF mRectF = new RectF(0, 0, 0, 0);
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setStyle(Style.STROKE);
            setColor(Color.RED);
            setStrokeWidth(mRingWidth);
        }
    };
    Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setStyle(Style.STROKE);
            setStrokeWidth(mRingWidth);
            setColor(Color.GRAY);
        }
    };
    private float progress = 0.5f;

    public JProgressView(Context context){
        super(context);
    }

    public JProgressView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
    }

    public JProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(mRingWidth/2, mRingWidth/2, w-mRingWidth/2, h-mRingWidth/2);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawArc(mRectF, 0, 360, false, mBgPaint);
        canvas.drawArc(mRectF, -90, progress*360, false, mPaint);
    }

    public JProgressView setProgress(float progress){
        this.progress = progress;
        postInvalidate();
        return this;
    }

    public JProgressView setProgColor(@ColorInt int color){
        mPaint.setColor(color);
        return this;
    }

    public JProgressView setProgWidth(float width){
        mRingWidth = width;
        mPaint.setStrokeWidth(mRingWidth);
        mBgPaint.setStrokeWidth(mRingWidth);
        return this;
    }


    public JProgressView setProgBgColor(@ColorInt int color){
        mBgPaint.setColor(color);
        return this;
    }
}
