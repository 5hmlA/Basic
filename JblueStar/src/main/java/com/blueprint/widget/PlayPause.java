package com.blueprint.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author yun.
 * @date 2016/12/11
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class PlayPause extends View implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {
    private static final long DURATION = 500;
    private float mSide;
    private PointF mCenter;
    private PointF mGa;
    private PointF mGb;
    private PointF mGc;
    private PointF mD;
    private PointF mE;
    private PointF mH;
    private PointF mI;
    private Path mPathR;
    private Path mPathL;
    private Paint mPaint;
    private PointF mLeft1;
    private PointF mLeft2;
    private PointF mRitht4;
    private PointF mRitht1;
    private PointF mRitht2;
    private PointF mRitht3;
    private ValueAnimator mAnimator;
    private float mRotate = 0;
    private OnStateChangeListener mListener;
    private float mPading;
    private boolean isPlay = true;
    private boolean mRotateAble = true;
    private float aniStart = 1/2f;
    private static final int NORMAL = 0;
    private static final int YOUTUBE = 1;
    private int mStyle = YOUTUBE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NORMAL, YOUTUBE})
    public @interface STYLE {}

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

    }

    public PlayPause(Context context){
        super(context);
    }

    public PlayPause(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public PlayPause(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w1, int h1, int oldw, int oldh){
        super.onSizeChanged(w1, h1, oldw, oldh);
        //        mPading = 0;
        mSide = w1-2*mPading;
        float w = mSide;
        float h = mSide;
        mCenter = new PointF(w/2f, h/2f);
        mGa = new PointF(0, 0);
        mGb = new PointF(w, h/2f);
        mGc = new PointF(0, h);
        mD = new PointF(w/2f, h/4f);
        mE = new PointF(w/2f, h*3f/4f);

        if(mStyle == NORMAL) {
            mH = new PointF(w*3f/4f, h*3f/8f);
            mI = new PointF(w*3f/4f, h*5f/8f);
        }else {
            mH = new PointF(mGb.x, mGb.y);
            mI = new PointF(mGb.x, mGb.y);
        }

        mLeft1 = new PointF(mD.x, mD.y);
        mLeft2 = new PointF(mE.x, mE.y);

        mRitht1 = new PointF(mD.x, mD.y);
        mRitht2 = new PointF(mE.x, mE.y);
        mRitht3 = new PointF(mH.x, mH.y);
        mRitht4 = new PointF(mI.x, mI.y);
        mPathL = new Path();
        mPathR = new Path();
        if(!isPlay) {
            calcuteL1(1f/3f*mSide);
            calcuteR3(( -3f/2f*1f/3f+3/2f )*mSide);
            calcuteR1(mLeft1);
            calcuteL2(mLeft1);
            calcuteR2(mLeft1);
            calcuteR4(mRitht3);
        }
        ConstructPath();

    }

    private void ConstructPath(){
        //左半部分
        if(!mPathL.isEmpty()) {
            mPathL.reset();
        }
        mPathL.moveTo(mGa.x, mGa.y);
        mPathL.lineTo(mLeft1.x, mLeft1.y);
        mPathL.lineTo(mLeft2.x, mLeft2.y);
        mPathL.lineTo(mGc.x, mGc.y);
        mPathL.close();
        //右半部份
        if(!mPathR.isEmpty()) {
            mPathR.reset();
        }
        mPathR.moveTo(mRitht1.x, mRitht1.y);
        mPathR.lineTo(mRitht3.x, mRitht3.y);
        mPathR.lineTo(mGb.x, mGb.y);
        mPathR.lineTo(mRitht4.x, mRitht4.y);
        mPathR.lineTo(mRitht2.x, mRitht2.y);
        mPathR.close();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.translate(mPading, mPading);
        if(mRotateAble) {
            canvas.rotate(mRotate, mCenter.x, mCenter.y);
        }
        canvas.drawPath(mPathL, mPaint);
        canvas.drawPath(mPathR, mPaint);
    }

    //和 数学方程一致
    private void calcuteL1(float x){//d
        mLeft1.x = x;
        //        mLeft1.y = x-mSide/4f;
        mLeft1.y = 3f/2f*x-mSide/2f;
    }

    private void calcuteR3(float x){//h
        if(mStyle == NORMAL) {
            mRitht3.x = x;
            mRitht3.y = -1.5f*x+1.5f*mSide;
        }else {
            mRitht3.x = mGb.x;
            //0--3/8  0--4/8===>(0,0)-(3/8,4/8)
            mRitht3.y = ( -1.5f*x+1.5f*mSide )*4/3f;
        }

    }

    private void calcuteR4(PointF Ritht3){//mRitht4
        mRitht4.set(Ritht3.x, mSide-Ritht3.y);
    }

    private void calcuteL2(PointF left1){
        mLeft2.set(left1.x, mSide-left1.y);
    }

    private void calcuteR1(PointF left1){
        mRitht1.set(mSide-left1.x, left1.y);
    }

    private void calcuteR2(PointF left1){
        mRitht2.set(mSide-left1.x, mSide-left1.y);
    }

    @Override
    public void onClick(View v){
        if(isPlay = !isPlay) {
            aniStart = Float.compare(aniStart, 1/3f)<0.00001f ? 1/3f : aniStart;
            //pause --- play
            //            mAnimator = ValueAnimator.ofFloat(1/3f, 1/2f);
            mAnimator = ValueAnimator.ofFloat(aniStart, 1/2f);
            mAnimator.setDuration(DURATION);
            mAnimator.addUpdateListener(this);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.start();
        }else {
            aniStart = Float.compare(1/2f, aniStart)<0.00001f ? 1/2f : aniStart;
            //play --- pause
            //            mAnimator = ValueAnimator.ofFloat(1/2f, 1/3f);
            mAnimator = ValueAnimator.ofFloat(aniStart, 1/3f);
            mAnimator.setDuration(DURATION);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.addUpdateListener(this);
            mAnimator.start();
        }
        if(mListener != null) {
            mListener.stateChanged(isPlay);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation){
        float animatedValue = aniStart = (float)animation.getAnimatedValue();
        if(mRotateAble) {
            mRotate = 540-1080*animatedValue;
        }
        calcuteL1(animatedValue*mSide);
        calcuteR3(( -3f/2f*animatedValue+3/2f )*mSide);
        calcuteR1(mLeft1);
        calcuteL2(mLeft1);
        calcuteR2(mLeft1);
        calcuteR4(mRitht3);
        ConstructPath();
        postInvalidate();
    }

    public PlayPause setPauseFirst(){
        aniStart = 1/3f;
        isPlay = false;
        return this;
    }

    public PlayPause setOnStateChangeListener(OnStateChangeListener listener){
        mListener = listener;
        return this;
    }

    public interface OnStateChangeListener {
        void stateChanged(boolean play);
    }

    public int getStyle(){
        return mStyle;
    }

    public PlayPause setStyle(@STYLE int style){
        mStyle = style;
        return this;
    }

    public PlayPause setRotateAble(boolean rotateAble){
        mRotateAble = rotateAble;
        return this;
    }

    public PlayPause setPading(float pading){
        mPading = pading;
        return this;
    }

}
