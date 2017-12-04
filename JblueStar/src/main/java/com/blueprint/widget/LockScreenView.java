package com.blueprint.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class LockScreenView extends RelativeLayout {

    public static interface OnSlidingListener{
        void onSliding(float offset);

        void onSlidingFinish();
    }

    private ViewDragHelper mDragHelper;
    private LockTextView mDragView;
    private Point mAutoBackPoint;
    private int mWidth;
    private OnSlidingListener mL;

    public LockScreenView(Context context) {
        super(context);
        init(context);
    }

    public LockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LockScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDragView = new LockTextView(context);
        addView(mDragView);
        int textpading = 15;
        int textpadingb = 15;
        LayoutParams layoutParams = (LayoutParams) mDragView.getLayoutParams();
        layoutParams.addRule(CENTER_HORIZONTAL);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = textpading;
        mDragView.setText("滑动解锁");
        mDragView.setPadding(textpading, textpading, textpading, textpadingb);
        mDragView.setTextSize(15);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft() + 10;
                final int rightBound = getWidth() - mDragView.getWidth() - leftBound;
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                setAlpha(1 - Math.abs((newLeft - mAutoBackPoint.x) * 1f / mAutoBackPoint.x));
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return mDragView.getTop();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (Math.abs(mDragView.getLeft() - mAutoBackPoint.x) < mWidth / 4 - mDragView.getWidth() / 2) {
                    mDragHelper.settleCapturedViewAt(mAutoBackPoint.x, mAutoBackPoint.y);
                } else {
                    if (mAutoBackPoint.x < mDragView.getLeft()) {
                        mDragHelper.settleCapturedViewAt(mWidth - mDragView.getWidth(), mAutoBackPoint.y);
                    } else {
                        mDragHelper.settleCapturedViewAt(0, mAutoBackPoint.y);
                    }
                }
                postInvalidate();
            }

        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mAutoBackPoint = new Point(mDragView.getLeft(), mDragView.getTop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            //刷新界面就好
            postInvalidate();
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mDragView.getWidth() - leftBound;
            final int newLeft = Math.min(Math.max(mDragView.getLeft(), leftBound), rightBound);
            setAlpha(1 - Math.abs((newLeft - mAutoBackPoint.x) * 1f / mAutoBackPoint.x));
            if (mL != null) {
                mL.onSliding(mDragView.getLeft());
                if (mDragView.getLeft() == mWidth - mDragView.getWidth() || mDragView.getLeft() == 0) {
                    mL.onSlidingFinish();
                }
            }
        }
    }

    public void setOnSlidingListener(OnSlidingListener l) {
        mL = l;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            setAlpha(1);
        }
    }
}
