package com.blueprint.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author yun.
 * @date 2017/7/16
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class DampLayout extends RelativeLayout {
    public DampLayout(Context context){
        super(context);
    }

    public DampLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public DampLayout(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP) {

        }
        return super.onTouchEvent(event);
    }
}
