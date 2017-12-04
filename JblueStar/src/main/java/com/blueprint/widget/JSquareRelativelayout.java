package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @another 江祖赟
 * @date 2017/10/17 0017.
 */
public class JSquareRelativelayout extends RelativeLayout {

    private RectF mRoundConorRectf = new RectF();
    private Path mRoundConorPath = new Path();
    public int mRoundConor = 0;

    public JSquareRelativelayout(Context context){
        super(context);
    }

    public JSquareRelativelayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public JSquareRelativelayout(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int min = Math.min(getWidth(), getHeight());
        if(min>0) {
            setMeasuredDimension(min, min);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mRoundConorRectf.set(getPaddingLeft(), getPaddingTop(), w-getPaddingRight(), h-getPaddingBottom());
        mRoundConorPath.addRoundRect(mRoundConorRectf, mRoundConor, mRoundConor, Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime){
        if(mRoundConor>0) {
            canvas.clipPath(mRoundConorPath);
        }
        return super.drawChild(canvas, child, drawingTime);
    }
}
