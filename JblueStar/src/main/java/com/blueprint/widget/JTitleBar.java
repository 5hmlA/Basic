package com.blueprint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.blueprint.helper.DpHelper.dp2px;
import static com.blueprint.helper.StatusBarHelper.getStatusBarHeight;
import static com.blueprint.helper.StrHelper.nullStrToEmpty;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class JTitleBar extends RelativeLayout {

    TextView mJtitlebarTitle;
    ImageView mJtitlebarLeftButton;
    ImageView mJtitlebarRightButton;
    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private int mTextColor;
    private String mTextContent;
    private int mTextSize;
    private boolean mIsfitsSystemWindows;

    public JTitleBar(Context context){
        this(context, null);
    }

    public JTitleBar(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JTitleBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setClickable(true);
        inflate(getContext(), com.blueprint.R.layout.custom_titlebar, this);
        TypedArray a = context.obtainStyledAttributes(attrs, com.blueprint.R.styleable.jtitlebar);
        mLeftDrawable = a.getDrawable(com.blueprint.R.styleable.jtitlebar_leftIcon);
        mRightDrawable = a.getDrawable(com.blueprint.R.styleable.jtitlebar_rightIcon);
        mTextColor = a.getColor(com.blueprint.R.styleable.jtitlebar_textColor, Color.WHITE);
        mTextSize = a.getColor(com.blueprint.R.styleable.jtitlebar_textSize, (int)dp2px(13));
        mTextContent = a.getString(com.blueprint.R.styleable.jtitlebar_text);
        mIsfitsSystemWindows = a.getBoolean(com.blueprint.R.styleable.jtitlebar_fitsSystemWindows, true);
        a.recycle();

        mJtitlebarTitle = (TextView)findViewById(com.blueprint.R.id.jtitlebar_title);
        mJtitlebarLeftButton = (ImageView)findViewById(com.blueprint.R.id.jtitlebar_left_button);
        mJtitlebarRightButton = (ImageView)findViewById(com.blueprint.R.id.jtitlebar_right_button);
        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            View fitSystem = findViewById(com.blueprint.R.id.jtitlebar_pading_status);
            ViewGroup.LayoutParams layoutParams = fitSystem.getLayoutParams();
            layoutParams.height = getStatusBarHeight();
        }
        mJtitlebarTitle.setTextSize(mTextSize);
        mJtitlebarTitle.setTextColor(mTextColor);
        if(mTextContent != null) {
            mJtitlebarTitle.setText(mTextContent);
        }
        if(mLeftDrawable != null) {
            mJtitlebarLeftButton.setImageDrawable(mLeftDrawable);
        }
        if(mRightDrawable != null) {
            mJtitlebarLeftButton.setImageDrawable(mRightDrawable);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp2px(8));
        }
    }

    public JTitleBar setTitle(@NonNull String title){
        if(getVisibility() != View.GONE) {
            mJtitlebarTitle.setText(nullStrToEmpty(title));
        }
        return this;
    }

    public JTitleBar setTitleSize(@NonNull int size){
        mJtitlebarTitle.setTextSize(size);
        return this;
    }

    public JTitleBar setTitleColor(@ColorInt int titleColor){
        mJtitlebarTitle.setTextColor(titleColor);
        return this;
    }

    public ImageView getLiftIcon(){
        return mJtitlebarLeftButton;
    }

    public ImageView getRightIcon(){
        return mJtitlebarRightButton;
    }

    public TextView getTitleTextView(){
        return mJtitlebarTitle;
    }
}
