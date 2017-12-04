package com.blueprint.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blueprint.helper.LogHelper;

/**
 * @another 江祖赟
 * @date 2017/7/7.
 * @Description: 可折叠的textview
 * @Others: <p><a href="https://github.com/ZuYun">github</a>
 * ====================================================
 * android:textColor="@color/colorAccent"
 * android:lineSpacingMultiplier="4"
 * android:lineSpacingExtra="4"
 * android:letterSpacing="3"
 * android:textSize="18dp"
 * android:maxLines="3"
 * android:text="折叠"
 * ======================================
 */
public class JExpandableTextViews extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "JExpandableTextViews";
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor, android.R.attr.lineSpacingExtra, android.R.attr.letterSpacing, android.R.attr.lineSpacingMultiplier};
    private static final int[] ATTRS2 = new int[]{android.R.attr.text, android.R.attr.maxLines};
    //private static final String TAG = JExpandableTextViews.class.getSimpleName();
    private int mSpacingAdd = 2;
    private int mTextColor = Color.DKGRAY;
    private int mTextSize = 15;

    public TextView mExpandTextView;//展开的文字/原文字 长布局
    public TextView mShowPartTextView;//限定长度显示的文字 短布局
    private Paint mWormViewPaint;//展开 提示信息
    private float letterSpacing = 0;
    private float mSpacingMult = 1.2f;
    private Context mContext;
    private int mMaxLines = 2;
    private CharSequence mContent;
    /**
     * 当前状态
     */
    private boolean isExpanded;
    private float mHeight;
    private float mWidth;
    private RectF mWormTvBg = new RectF();
    private PointF mWormTvCenter = new PointF(0, 0);
    private float mWormTvSize = 15;
    /**
     * 提示下一步隐藏
     */
    private String mHideMsg = "隐藏";
    /**
     * 提示下一步展开
     */
    private String mExpandMsg = "展开";
    private Paint mWormBGPaint;
    private int tvAlpha = 255;
    private int mWormTvPosition = Gravity.RIGHT;
    private int mBackColor = Color.WHITE;
    private int mWormTvColor = Color.GREEN;
    private boolean needExpandable;
    private int mPaddingBottom;
    private boolean mAnimateable;
    private String mOrignPartshowString = "";
    private float mLastLineLength;
    public static final String MORE_CONTENT = "…";


    public JExpandableTextViews(Context context){
        super(context);
        mContext = context;
        init();
    }

    public JExpandableTextViews(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JExpandableTextViews(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;
        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mWormTvSize = mTextSize = a.getDimensionPixelSize(0, mTextSize);
        mTextColor = a.getColor(1, mTextColor);
        mSpacingAdd = a.getDimensionPixelSize(2, (int)mSpacingAdd);
//        letterSpacing = a.getFloat(3, 0);
        mSpacingMult = a.getFloat(4, mSpacingMult);
        //        mContent = a.getText(5);//很奇怪读不到; 只能5个?
        //        mMaxLines = a.getInt(6, mMaxLines);//很奇怪读不到
        a.recycle();

        TypedArray a2 = context.obtainStyledAttributes(attrs, ATTRS2);
        mContent = a2.getText(0);
        mMaxLines = a2.getInt(1, mMaxLines);
        a2.recycle();

        init();
        setOnClickListener(this);
    }

    private void init(){
        mExpandTextView = new AppCompatTextView(mContext) {
            {
                setTextColor(mTextColor);
                setVisibility(GONE);
            }
        };
        mShowPartTextView = new AppCompatTextView(mContext) {
            {
                setTextColor(mTextColor);
                setEllipsize(TextUtils.TruncateAt.END);
                setMaxLines(mMaxLines);
            }
        };
        mWormViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(mWormTvColor);
                setTextAlign(Align.CENTER);
            }
        };
        mWormBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setDither(true);
                setColor(Color.WHITE);
                setDither(true);
                setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.NORMAL));
            }
        };

        configTextViews();
        //        if(mAnimateable) {
        //            setLayoutTransition(new LayoutTransition());
        //        }
        mPaddingBottom = getPaddingBottom();
        setBackgroundColor(mBackColor);
        addView(mShowPartTextView);
        addView(mExpandTextView);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
    }


    /**
     * 提示文字 高的1.6倍
     *
     * @return
     */
    private int getBottomOffset(){
        return (int)( getFontHeight(mWormViewPaint)*1.6 );
    }

    private void configTextViews(){
        feedTextView(mExpandTextView);
        feedTextView(mShowPartTextView);
        float textSize = mShowPartTextView.getTextSize();
        mWormTvSize = textSize;
        mWormViewPaint.setTextSize(mWormTvSize);
    }

    private void feedTextView(TextView textView){
        textView.setText(mContent);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            textView.setLetterSpacing(letterSpacing);
        }
        textView.setLineSpacing(mSpacingAdd, mSpacingMult);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setTextColor(mTextColor);
        textView.setLayoutParams(new LayoutParams(-1, -2));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        configWormTv();
        getShowpartTextString();
        //getEllipisisCount(mShowPartTextView);
        if(mContent != null) {
            //thanks:https://stackoverflow.com/questions/4005933/how-do-i-tell-if-my-textview-has-been-ellipsized
            needExpandable = !mContent.toString().equals(mOrignPartshowString);
        }else {
            needExpandable = false;
        }

        if(needExpandable && isPartViewSafe() && mShowPartTextView.getLayout().getText().toString()
                .equals(mOrignPartshowString)) {
            //System.out.println(mOrignPartshowString.indexOf("…\uFEFF"));
            int parttextlength = mOrignPartshowString.indexOf(MORE_CONTENT);
            parttextlength = parttextlength>0 ? parttextlength : mOrignPartshowString.length()-3;
            LogHelper.slog_d(TAG, "原内容:=="+mOrignPartshowString.substring(0, parttextlength));//不包括…
            int cut = getNeedCutNum(parttextlength);
            LogHelper.slog_d(TAG, "调整后的内容:=="+mOrignPartshowString.substring(0, parttextlength-cut));//不包括…
            //mHideTextView一定满行
            //去掉4个字左右 防止展开挡住部分文字//mOrignPartshowString.length()是完整的内容的长度
            mShowPartTextView.setText(mOrignPartshowString.substring(0, parttextlength-cut)+"….");

        }
    }


    private int getNeedCutNum(int parttextlength){
        float wormWidth = mWormTvBg.right-mWormTvBg.left;
        if(isPartViewSafe()) {
            int startPos = mShowPartTextView.getLayout().getLineStart(mMaxLines-1);
            int endPos = mShowPartTextView.getLayout().getLineEnd(mMaxLines-1);
            String lastLineString = mOrignPartshowString.substring(startPos, endPos);
            mLastLineLength = mWormViewPaint.measureText(lastLineString);
            float textOffset2Right = mWidth-getPaddingLeft()-mWormViewPaint.measureText(lastLineString);
            for(int i = 1; i<parttextlength; i++) {
                String substring = mOrignPartshowString.substring(parttextlength-i, parttextlength);//不包括神略好
                //if (mWormViewPaint.measureText(substring) >= wormWidth-textOffset2Right) {
                if(mWormViewPaint.measureText(substring)>=wormWidth-textOffset2Right) {
                    LogHelper.slog_d(TAG, "剪掉的文字:=="+substring);
                    return i;
                }
            }
        }
        return 0;
    }


    private String getShowpartTextString(){
        if(TextUtils.isEmpty(mOrignPartshowString) && isPartViewSafe()) {
            mOrignPartshowString = mShowPartTextView.getLayout().getText().toString();
        }
        return mOrignPartshowString;
    }

    private boolean isPartViewSafe(){
        return mShowPartTextView != null && mShowPartTextView.getLayout() != null && mShowPartTextView.getLayout()
                .getText() != null;
    }


    private void getEllipisisCount(TextView textview){
        Layout layout = textview.getLayout();
        if(layout != null) {
            int lines = layout.getLineCount();
            if(lines>0) {
                int ellipsisCount = layout.getEllipsisCount(lines-1);
                if(ellipsisCount>0) {
                    Log.d(TAG, "Text is ellipsized");
                }
            }
        }
    }

    private void configWormTv(){
        if(Gravity.BOTTOM == mWormTvPosition) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                    mPaddingBottom+getBottomOffset()+mSpacingAdd);
        }else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), mPaddingBottom);
        }
        String msg = getWormMsg();
        float wormWidth = mWormViewPaint.measureText(msg)*4/5f;
        if(Gravity.BOTTOM == mWormTvPosition) {
            mWormTvCenter.x = mWidth/2;
            mWormTvCenter.y = mHeight-( mPaddingBottom+getBottomOffset()+mSpacingAdd/2 )/2;

            mWormTvBg.set(mWormTvCenter.x-wormWidth, mWormTvCenter.y-getBottomOffset()/2f, mWormTvCenter.x+wormWidth,
                    mWormTvCenter.y+getBottomOffset()/2f);
        }else {
            mWormTvBg.set(mWidth-getPaddingRight()-2*wormWidth, mHeight-getPaddingRight()-getBottomOffset(),
                    mWidth-getPaddingRight(), mHeight-getPaddingBottom());
            //float v = mHeight / mShowPartTextView.getMaxLines();
            //mWormTvCenter.y = mHeight - v/2;
        }
        mWormTvCenter.x = ( mWormTvBg.left+mWormTvBg.right )/2f;
        mWormTvCenter.y = ( mWormTvBg.top+mWormTvBg.bottom )/2f;
    }

    private String getWormMsg(){
        return isExpanded ? mHideMsg : mExpandMsg;
    }


    @CallSuper
    public void draw(Canvas canvas){
        super.draw(canvas);
        // Step 1, draw the background, if needed
        //   1. Draw the background == drawBackground(canvas);
        //   2. If necessary, save the canvas' layers to prepare for fading
        //   3. Draw view's content == onDraw(Canvas canvas)
        //   4. Draw children ==== dispatchDraw(Canvas canvas)
        //   5. If necessary, draw the fading edges and restore layers  //fading edges
        //   6. Draw decorations (scrollbars for instance) == onDrawForeground(Canvas canvas)
        if(needExpandable) {
            mWormBGPaint.setAlpha(tvAlpha);
            canvas.drawRect(mWormTvBg, mWormBGPaint);
            mWormViewPaint.setAlpha(tvAlpha);
            canvas.drawText(getWormMsg(), mWormTvCenter.x, mWormTvCenter.y+getFontHeight(mWormViewPaint)/2,
                    mWormViewPaint);
        }
    }

    //    @Override
    //    protected void onDraw(Canvas canvas){
    //        super.onDraw(canvas);
    //        canvas.drawRect(mWormTvBg, mWormBGPaint);
    //        mWormViewPaint.setAlpha(tvAlpha);
    //        canvas.drawText(getWormMsg(), mWormTvCenter.x, mWormTvCenter.y+getFontHeight(mWormViewPaint)/2, mWormViewPaint);
    //    }

    //    @Override
    //    protected void dispatchDraw(Canvas canvas){
    //        super.dispatchDraw(canvas);
    //        mWormBGPaint.setAlpha(tvAlpha);
    //        canvas.drawRect(mWormTvBg, mWormBGPaint);
    //        mWormViewPaint.setAlpha(tvAlpha);
    //        canvas.drawText(getWormMsg(), mWormTvCenter.x, mWormTvCenter.y+getFontHeight(mWormViewPaint)/2, mWormViewPaint);
    //
    //    }

    //    @Override
    //    public void onDrawForeground(Canvas canvas){
    //        super.onDrawForeground(canvas);
    //        canvas.drawRect(mWormTvBg, mWormBGPaint);
    //        mWormViewPaint.setAlpha(tvAlpha);
    //        canvas.drawText(getWormMsg(), mWormTvCenter.x, mWormTvCenter.y+getFontHeight(mWormViewPaint)/2, mWormViewPaint);
    //
    //    }

    public JExpandableTextViews setMaxLines(int maxLines){
        mMaxLines = maxLines;
        configTextViews();
        return this;
    }

    public JExpandableTextViews setText(CharSequence content){
        mContent = content;
        mOrignPartshowString = null;
        mExpandTextView.setText(mContent);
        mShowPartTextView.setText(mContent);
        return this;
    }

    public boolean isExpanded(){
        return isExpanded;
    }

    public JExpandableTextViews setExpanded(boolean expanded){
        isExpanded = expanded;
        return this;
    }

    public int getSpacingAdd(){
        return mSpacingAdd;
    }

    public JExpandableTextViews setSpacingAdd(int spacingAdd){
        mSpacingAdd = spacingAdd;
        configTextViews();
        return this;
    }

    public int getTextColor(){
        return mTextColor;
    }

    public JExpandableTextViews setTextColor(int textColor){
        this.mTextColor = textColor;
        configTextViews();
        return this;
    }

    public int getTextSize(){
        return mTextSize;
    }

    /**
     * 单位 px
     *
     * @param textSize
     * @return
     */
    public JExpandableTextViews setTextSizePx(int textSize){
        this.mTextSize = textSize;
        configTextViews();
        return this;
    }

    public JExpandableTextViews setTextSize(int textSize){
        return setTextSizePx((int)TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize, getResources().getDisplayMetrics()));
    }

    public float getLetterSpacing(){
        return letterSpacing;
    }

    public JExpandableTextViews setLetterSpacing(float letterSpacing){
        this.letterSpacing = letterSpacing;
        configTextViews();
        return this;
    }

    public float getSpacingMult(){
        return mSpacingMult;
    }

    public JExpandableTextViews setSpacingMult(float spacingMult){
        mSpacingMult = spacingMult;
        configTextViews();
        return this;
    }

    public int getMaxLines(){
        return mMaxLines;
    }

    public float getWormTvSize(){
        return mWormTvSize;
    }

    public JExpandableTextViews setWormTvSize(float wormTvSize){
        if(wormTvSize>mTextSize) {
            Log.e(TAG, "字体大小过大 会挡住文本内容");
        }else {
            mWormTvSize = wormTvSize;
            mWormViewPaint.setTextSize(mWormTvSize);
        }
        return this;
    }

    public String getHideMsg(){
        return mHideMsg;
    }

    public JExpandableTextViews setHideMsg(String hideMsg){
        mHideMsg = hideMsg;
        return this;
    }

    public String getExpandMsg(){
        return mExpandMsg;
    }

    public JExpandableTextViews setExpandMsg(String expandMsg){
        mExpandMsg = expandMsg;
        return this;
    }

    public int getWormTvPosition(){
        return mWormTvPosition;
    }

    public JExpandableTextViews setWormTvPosition(int wormTvPosition){
        mWormTvPosition = wormTvPosition;
        return this;
    }

    public int getBackColor(){
        return mBackColor;
    }

    public JExpandableTextViews setBackColor(int backgroundColor){
        mBackColor = backgroundColor;
        mWormBGPaint.setColor(mBackColor);
        setBackgroundColor(mBackColor);
        return this;
    }

    public int getTvAlpha(){
        return tvAlpha;
    }

    public JExpandableTextViews setTvAlpha(int tvAlpha){
        this.tvAlpha = tvAlpha;
        return this;
    }

    public int getWormTvColor(){
        return mWormTvColor;
    }

    public JExpandableTextViews setWormTvColor(int wormTvColor){
        mWormTvColor = wormTvColor;
        mWormViewPaint.setColor(mWormTvColor);
        return this;
    }

    public JExpandableTextViews toggle(){
        if(needExpandable) {
            isExpanded = !isExpanded;
            if(isExpanded) {
                //展开的时候 提示内容移到最下面 防止可能的遮挡完整内容mExpandTextView
                mWormTvPosition = Gravity.BOTTOM;
                mExpandTextView.setVisibility(VISIBLE);
                mShowPartTextView.setVisibility(GONE);
            }else {
                mWormTvPosition = Gravity.RIGHT;
                mShowPartTextView.setVisibility(VISIBLE);
                mExpandTextView.setVisibility(GONE);
            }
            configWormTv();
            if(mAnimateable) {
                ObjectAnimator.ofInt(this, "tvAlpha", 10, 255).setDuration(300).start();
            }
        }
        return this;
    }

    public JExpandableTextViews toggle1(){
        if(needExpandable) {
            isExpanded = !isExpanded;
            if(isExpanded) {
                mExpandTextView.setVisibility(VISIBLE);
                mShowPartTextView.setVisibility(GONE);
            }else {
                mShowPartTextView.setVisibility(VISIBLE);
                mExpandTextView.setVisibility(GONE);
            }
            if(mAnimateable) {
                ObjectAnimator.ofInt(this, "tvAlpha", 10, 255).setDuration(300).start();
            }
        }
        return this;
    }

    public float getFontHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -fontMetrics.top-fontMetrics.bottom;
    }

    @Override
    public void onClick(View v){
        toggle();
    }

    public JExpandableTextViews setAnimateable(boolean animateable){
        mAnimateable = animateable;
        return this;
    }

    public boolean isAnimateable(){
        return mAnimateable;
    }

    public TextView[] getTextViews(){
        return new TextView[]{mExpandTextView, mShowPartTextView};
    }
}
