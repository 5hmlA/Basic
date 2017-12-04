package com.blueprint.helper;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * @another 江祖赟
 * @date 2017/11/25.
 */
public class PressTintedDrawableViewHelper implements View.OnTouchListener {
    private static final int MSG_TINT = 1;
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();

    private View target;
    private Drawable[] drawables;
    private int tintColor;

    private Handler handler;
    private float downX, downY;
    private int touchSlop;

    private boolean tinted = false;

    public PressTintedDrawableViewHelper(int tintColor){
        this.tintColor = tintColor;
    }

    public PressTintedDrawableViewHelper wrap(TextView textView){
        this.target = textView;
        this.drawables = textView.getCompoundDrawables();
        return this;
    }

    public PressTintedDrawableViewHelper wrap(ImageView imageView){
        this.target = imageView;
        this.drawables = new Drawable[]{imageView.getDrawable()};
        return this;
    }

    public boolean apply(){
        if(drawables != null && drawables.length>0) {
            handler = new TouchHandler(this);
            target.setOnTouchListener(this);

            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();

                handler.sendEmptyMessageDelayed(MSG_TINT, TAP_TIMEOUT);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX()-downX;
                float dy = event.getY()-downY;
                if(touchSlop == 0) {
                    touchSlop = ViewConfiguration.get(target.getContext()).getScaledTouchSlop();
                }
                if(( dx*dx )+( dy*dy )>( touchSlop*touchSlop )) {
                    handler.removeMessages(MSG_TINT);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!tinted) {
                    if(handler.hasMessages(MSG_TINT)) {
                        handler.removeMessages(MSG_TINT);
                        applyTint();
                        target.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                clearTint();
                            }
                        }, ViewConfiguration.getPressedStateDuration());
                    }
                }else {
                    clearTint();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                clearTint();
                handler.removeMessages(MSG_TINT);
                break;
        }

        return false;
    }

    private void applyTint(){
        ColorFilter colorFilter = new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
        for(Drawable drawable : drawables) {
            if(drawable != null) {
                drawable.mutate().setColorFilter(colorFilter);
            }
        }
        tinted = true;
    }

    private void clearTint(){
        if(tinted) {
            for(Drawable drawable : drawables) {
                if(drawable != null) {
                    drawable.mutate().clearColorFilter();
                }
            }
            tinted = false;
        }
    }

    private static class TouchHandler extends Handler {
        WeakReference<PressTintedDrawableViewHelper> ref;

        public TouchHandler(PressTintedDrawableViewHelper view){
            this.ref = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg){
            switch(msg.what) {
                case MSG_TINT:
                    PressTintedDrawableViewHelper view = ref.get();
                    if(view != null) {
                        view.applyTint();
                    }
                    break;
            }
        }
    }
}
