package com.baselib.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class JViewPager extends ViewPager {

    private boolean noScrollable = true;

    public JViewPager(Context context){
        super(context);
    }

    public JViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(noScrollable) {
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if(noScrollable) {
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void setNoScrollable(boolean noScrollable){
        this.noScrollable = noScrollable;
    }
}
