package com.blueprint.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @another 江祖赟
 * @date 2017/7/27 0027.
 */
public class FixedSpeedScroller extends Scroller {

    private int mDuration = 1000;

    public FixedSpeedScroller(Context context){
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator){
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, int period){
        this(context, interpolator);
        mDuration = period;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration){
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy){
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
