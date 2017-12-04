package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.blueprint.R;

/**
 * @another 江祖赟
 * @date 2017/11/10 0010.
 */
public class JCompoundButton extends CompoundButton {

    private Drawable mButtonDrawable;

    public JCompoundButton(Context context){
        this(context, null);
    }

    public JCompoundButton(Context context, AttributeSet attrs){
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public JCompoundButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        //        this(context, attrs, defStyleAttr, 0);
    }

//    public JCompoundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    /**
     * {@inheritDoc}
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    @Override
    public void toggle(){
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if(!isChecked()) {
            super.toggle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        final Drawable buttonDrawable = getButtonDrawable();
        if(buttonDrawable != null) {
            final int horizontalGravity = getGravity()&Gravity.HORIZONTAL_GRAVITY_MASK;
            final int drawableWidth = buttonDrawable.getIntrinsicWidth();

            final int left;
            switch(horizontalGravity) {
                case Gravity.RIGHT:
                    left = getWidth()-drawableWidth;
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    left = ( getWidth()-drawableWidth )/2;
                    break;
                default:
                    left = 0;
            }
            canvas.translate(left,0);
//            final int right = left+drawableWidth;
//            Rect bounds = buttonDrawable.getBounds();
//            buttonDrawable.setBounds(left, bounds.top, right, bounds.bottom);
//            buttonDrawable.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setButtonDrawable(int resId){
        super.setButtonDrawable(resId);

    }

    @Nullable
    @Override
    public Drawable getButtonDrawable(){
        return mButtonDrawable;
    }

    @Override
    public void setButtonDrawable(Drawable drawable){
        super.setButtonDrawable(drawable);
        mButtonDrawable = drawable;
    }

    @Override
    public CharSequence getAccessibilityClassName(){
        return RadioButton.class.getName();
    }
}
