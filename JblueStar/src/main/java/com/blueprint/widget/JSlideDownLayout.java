package com.blueprint.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

 /**
 * @author 江祖赟.
 * @date 2017/6/7
 * @Description: 支持上下滑动的布局
 * @Others:
  * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JSlideDownLayout extends RelativeLayout {

    private ViewDragHelper mDragHelper;
    private View mDragView;
    private Point mAutoBackPoint;
    private int mHeight;
    private OnSlidingListener mL;

    public JSlideDownLayout(Context context){
        super(context);
        init(context);
    }

    public JSlideDownLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public JSlideDownLayout(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mDragView = getChildAt(0);
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId){
                //        mDragView = child;
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx){
                return mDragView.getLeft();
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy){
                if(top>0) {
                    return top;
                }else {
                    return mDragView.getTop();
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel){
                if(Math.abs(mDragView.getTop()-mAutoBackPoint.y)<mHeight/3) {
                    mDragHelper.settleCapturedViewAt(mAutoBackPoint.x, mAutoBackPoint.y);
                }else {
                    mDragHelper.settleCapturedViewAt(mAutoBackPoint.x, mHeight);
                }
                postInvalidate();
            }

        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        mAutoBackPoint = new Point(mDragView.getLeft(), mDragView.getTop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll(){
        if(mDragHelper.continueSettling(true)) {
            //刷新界面就好
            postInvalidate();

            if(mL != null) {
                mL.onSliding(mDragView.getTop());
                if(mDragView.getTop() == mHeight) {
                    mL.onSlidingFinish();
                }
            }
        }
    }

    public void setOnSlidingListener(OnSlidingListener l){
        mL = l;
    }

    public interface OnSlidingListener {
        void onSliding(float offset);

        void onSlidingFinish();
    }

}
