package com.blueprint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
@SuppressLint("AppCompatCustomView")
public class LockTextView extends TextView {

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0;
    private int mTranslate = 0;

    private boolean mAnimating = true;

    public LockTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockTextView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            // getWidth得到是某个view的实际尺寸.
            // getMeasuredWidth是得到某view想要在parent view里面占的大小.
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                // 线性渐变
                mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, 0, new int[]
                        {0x80000000, 0xffffffff, 0x80000000}, new float[]
                        {0, 0.5f, 1}, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimating && mGradientMatrix != null) {
            //每一次运动的递增值
            mTranslate += mViewWidth / 10;
            //结束条件语句：当递增值大于两倍child view宽度时候，及回到屏幕的左边
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            // 50ms刷新一次
            postInvalidateDelayed(50);
        }
    }

}