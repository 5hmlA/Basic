package com.blueprint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blueprint.helper.interf.DoubleClickAble;
import com.blueprint.helper.interf.JSimpleOnGestureListener;
import com.blueprint.rx.ViewDoubleClickObservable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static com.blueprint.helper.DpHelper.dp2px;
import static com.blueprint.helper.DpHelper.dp2pxCeilInt;
import static com.blueprint.helper.StatusBarHelper.getStatusBarHeight;
import static com.blueprint.helper.StrHelper.safeObject2Str;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 * <p><a href="https://github.com/ZuYun">github</a>
 * ====================================================
 * android:drawableLeft="@drawable/icon_btn_back"
 * android:drawableRight="@drawable/icon_btn_back"
 * android:textColor="@color/colorAccent"
 * android:fitsSystemWindows="true"
 * android:textSize="18dp"
 * android:text="测试"
 * ======================================
 */
public class JTitleBar extends RelativeLayout implements DoubleClickAble, GestureDetector.OnDoubleTapListener {

    private static final int[] ATTRS2 = new int[]{android.R.attr.text, android.R.attr.drawableLeft, android.R.attr.drawableRight};
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fitsSystemWindows};
    private final RelativeLayout mRealTitleBar;

    TextView mJtitlebarTitle;
    View mJtitlebarLeftButton;
    View mJtitlebarRightButton;
    private Drawable mLeftDrawable;
    private Drawable mRightDrawable;
    private int mTextColor;
    private CharSequence mTextContent;
    private int mTextSize = 15;
    private boolean mIsfitsSystemWindows = true;
    private int mCustomTitlelayoutRes;
    private View mCustomTitleView;
    private View mCustomRightView;
    private LayoutParams mCustRightlayoutParams;
    private LayoutParams mCustLeftlayoutParams;
    private View mCustomLeftView;
    private GestureDetector mGestureDetector;
    @Nullable private OnClickListener mClickListener;
    private OnDoubleClickListener mDoubleClickListener;
    private int mOrignPaddingTop;

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
        TypedArray sa = context.obtainStyledAttributes(attrs, ATTRS2);
        mTextContent = sa.getText(0);
        mLeftDrawable = sa.getDrawable(1);
        mRightDrawable = sa.getDrawable(2);
        sa.recycle();

        TypedArray sa1 = context.obtainStyledAttributes(attrs, ATTRS);
        mTextSize = sa1.getDimensionPixelSize(0, dp2px(getContext(), mTextSize));
        mTextColor = sa1.getColor(1, Color.WHITE);
        mIsfitsSystemWindows = sa1.getBoolean(2, true);
        sa1.recycle();

        mJtitlebarTitle = (TextView)findViewById(com.blueprint.R.id.jtitlebar_title);
        mRealTitleBar = (RelativeLayout)findViewById(com.blueprint.R.id.jtitlebar_real_titlebar);
        mJtitlebarLeftButton = (ImageView)findViewById(com.blueprint.R.id.jtitlebar_left_button);
        mJtitlebarRightButton = (ImageView)findViewById(com.blueprint.R.id.jtitlebar_right_button);
        //如果再activity里面设置fitsSystemWindows 会出问题 布局会自己加上pading 如果在fragment里面正常
        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            //            View fitSystem = findViewById(com.blueprint.R.id.jtitlebar_pading_status);
            //            ViewGroup.LayoutParams layoutParams = fitSystem.getLayoutParams();
            //            layoutParams.height = getStatusBarHeight();
            //fix：activity里面设置fitsSystemWindows
            mOrignPaddingTop = getPaddingTop();
            setPadding(getPaddingLeft(), mOrignPaddingTop+getStatusBarHeight(), getPaddingRight(), getPaddingBottom());
        }
        mJtitlebarTitle.setTextColor(mTextColor);
        if(mTextContent != null) {
            mJtitlebarTitle.setText(mTextContent);
        }
        if(mLeftDrawable != null) {
            ( (ImageView)mJtitlebarLeftButton ).setImageDrawable(mLeftDrawable);
        }
        if(mRightDrawable != null) {
            ( (ImageView)mJtitlebarRightButton ).setImageDrawable(mRightDrawable);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp2px(8));
        }
        mJtitlebarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

        mGestureDetector = new GestureDetector(getContext(), new JSimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        int hpading = Math.max(mJtitlebarLeftButton.getMeasuredWidth()+getPaddingLeft(),
                getPaddingRight()+mJtitlebarRightButton.getMeasuredWidth());
        RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarTitle.getLayoutParams();
        layoutParams.leftMargin = hpading;
        layoutParams.rightMargin = hpading;
        //        mJtitlebarTitle.setBackgroundColor(Color.GRAY);
    }

    //    @Override
    //    protected void onLayout(boolean changed, int l, int t, int r, int b){
    //        super.onLayout(changed, l, t, r, b);
    ////        int hpading = Math.max(mJtitlebarLeftButton.getMeasuredWidth()+getPaddingLeft(),
    ////                getPaddingRight()+mJtitlebarRightButton.getMeasuredWidth());
    ////        RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarTitle.getLayoutParams();
    ////        layoutParams.leftMargin = hpading;
    ////        layoutParams.rightMargin = hpading;
    //    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mGestureDetector != null) {
            //GestureDetector导致系统的点击事件失效
            return mGestureDetector.onTouchEvent(event);
        }else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        //查看是否有自定义titlebar
        if(mCustomTitlelayoutRes != 0) {
            mCustomTitleView = inflate(getContext(), mCustomTitlelayoutRes, mRealTitleBar);
        }else if(mCustomTitleView != null) {
            mRealTitleBar.removeAllViews();
            mRealTitleBar.addView(mCustomTitleView, new RelativeLayout.LayoutParams(-1, -1));
        }
        if(mCustomRightView != null) {
            if(mCustRightlayoutParams == null) {
                RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarRightButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                mCustRightlayoutParams = layoutParams;
            }
            mRealTitleBar.removeView(mJtitlebarRightButton);
            //            mCustomRightView.setId(com.blueprint.R.id.jtitlebar_right_button);
            mRealTitleBar.addView(mCustomRightView, mCustRightlayoutParams);
            mJtitlebarRightButton = mCustomRightView;
            requestLayout();
        }
        if(mCustomLeftView != null) {
            if(mCustLeftlayoutParams == null) {
                RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarLeftButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                mCustLeftlayoutParams = layoutParams;
            }
            mRealTitleBar.removeView(mJtitlebarLeftButton);
            //            mCustomLeftView.setId(com.blueprint.R.id.jtitlebar_left_button);
            mRealTitleBar.addView(mCustomLeftView, mCustLeftlayoutParams);
            mJtitlebarLeftButton = mCustomLeftView;
            requestLayout();
        }
    }

    public JTitleBar setTitle(@NonNull String title){
        if(getVisibility() != View.GONE) {
            mJtitlebarTitle.setText(safeObject2Str(title));
        }
        return this;
    }

    public JTitleBar setTitleSize(@NonNull int size){
        mJtitlebarTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        return this;
    }

    public JTitleBar setTitleSizePX(@NonNull int size){
        mJtitlebarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        return this;
    }

    public JTitleBar setTitleColor(@ColorInt int titleColor){
        mJtitlebarTitle.setTextColor(titleColor);
        return this;
    }

    public View getLeftIcon(){
        return mJtitlebarLeftButton;
    }

    /**
     * default Imageview
     *
     * @return
     */
    public View getRightIcon(){
        return mJtitlebarRightButton;
    }

    public TextView getTitleTextView(){
        return mJtitlebarTitle;
    }

    public JTitleBar setText(String title){
        mJtitlebarTitle.setText(title);
        return this;
    }

    public String getText(){
        if(mJtitlebarTitle.getText() != null) {
            return mJtitlebarTitle.getText().toString();
        }else {
            return "";
        }
    }

    public void replaceTitleContent(int customTitlelayoutRes){
        mCustomTitlelayoutRes = customTitlelayoutRes;
    }

    public void replaceTitleContent(View customTitleView){
        mCustomTitleView = customTitleView;
    }

    public RelativeLayout getRealTitleBar(){
        return mRealTitleBar;
    }

    public void replaceLeftView(View leftView){
        mCustomLeftView = leftView;
    }

    /**
     * @param rightContent
     * @return 返回right的textview 可以设置字体大小etc
     */
    public TextView replaceLeftAsTextView(String rightContent){
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(dp2pxCeilInt(14), 0, 0, 0);
        textView.setText(rightContent);
        return (TextView)( mCustomLeftView = textView );
    }

    /**
     * 默认right是图片
     * 默认布局 参数 -2,-1,RelativeLayout.CENTER_VERTICAL,建议自己设置gravity
     *
     * @param rightView
     */
    public void replaceRightView(View rightView){
        mCustomRightView = rightView;
    }

    /**
     * @param rightContent
     * @return 返回right的textview 可以设置字体大小etc
     */
    public TextView replaceRightAsTextView(String rightContent){
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(0, 0, dp2pxCeilInt(14), 0);
        textView.setText(rightContent);
        return (TextView)( mCustomRightView = textView );
    }

    /**
     * @param rightContent
     * @return 返回right的textview 可以设置字体大小etc
     */
    public CheckBox replaceRightAsCheckBox(String rightContent){
        CheckBox textView = new CheckBox(getContext());
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(0, 0, dp2pxCeilInt(14), 0);
        textView.setText(rightContent);
        return (CheckBox)( mCustomRightView = textView );
    }

    /**
     * 默认right是图片
     *
     * @param rightView
     */
    public void replaceRightView(View rightView, RelativeLayout.LayoutParams custRightlayoutParams){
        mCustomRightView = rightView;
        mCustRightlayoutParams = custRightlayoutParams;
    }

    /**
     * 默认right是图片
     *
     * @param leftView
     */
    public void replaceLeftView(View leftView, RelativeLayout.LayoutParams custLeftlayoutParams){
        mCustomLeftView = leftView;
        mCustLeftlayoutParams = custLeftlayoutParams;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e){
        if(mClickListener != null) {
            mClickListener.onClick(this);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent e){
        if(mDoubleClickListener != null) {
            mDoubleClickListener.onDoubleClicked(this);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e){
        return false;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l){
        super.setOnClickListener(l);
        mGestureDetector = null;
    }

    /**
     * 使用GestureDetector的onSingleTapConfirmed作为点击事件
     *
     * @param l
     */
    public void setOnClickListener2(@Nullable OnClickListener l){
        mClickListener = l;
    }

    public void setOnDoubleClickListener(DoubleClickAble.OnDoubleClickListener dl){
        mDoubleClickListener = dl;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(null != mGestureDetector) {
            mGestureDetector.setOnDoubleTapListener(null);
        }
    }

    public Observable rxDoubleClick(){
        return new ViewDoubleClickObservable(this).throttleFirst(1, TimeUnit.SECONDS);
    }

    public void removeFitSystemWindow(){
        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            setPadding(getPaddingLeft(), mOrignPaddingTop, getPaddingRight(), getPaddingBottom());
        }
    }
}
