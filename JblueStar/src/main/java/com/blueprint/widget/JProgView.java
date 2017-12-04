package com.blueprint.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.blueprint.helper.DpHelper;

import java.text.NumberFormat;

import static com.blueprint.helper.DrawHelper.getFontHeight;


/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JProgView extends View {

    private int mWidth;
    private RectF mOutRectF = new RectF();
    private Paint mOutPaint;
    private RectF mProRectF = new RectF();
    private Paint mProgPaint;
    private float mProg;
    private int mPading;
    private Path mOutPath;
    private int mPColor = Color.RED;
    private int mSecPColor = Color.GREEN;

    private int mTPColor = Color.WHITE;
    private int mSecTPColor;

    /**
     * 默认-1 为半圆
     */
    private float radio = -1;
    private Matrix mShaderMatric;
    private LinearGradient mLinearGradient;
    private float mTransx;
    private Matrix mProgMatrix = new Matrix();
    private Path mProgPath;
    private LinearGradient mProgLinearGradient;
    private LinearGradient mProgTextLinearGradient;
    private Paint mTextPaint;
    private NumberFormat mPercentFormat;
    private PointF mCenter = new PointF(0, 0);
    private Drawable mBackground;
    private String[] mShowMsgs = new String[]{"下载", "完成"};
    private String mShow;
    private static final int TYPE_WIDTH = 1;
    private static final int TYPE_HEIGHT = 11;
    private int mHeight;

    {
        mOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(Color.GRAY);
                setStrokeWidth(3);
                setStyle(Style.STROKE);
            }
        };
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setTextSize(DpHelper.dp2px(12));
                setTextAlign(Align.CENTER);
            }
        };
        mProgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPercentFormat = NumberFormat.getPercentInstance();
        mSecTPColor = mPColor;
    }

    public JProgView(Context context){
        super(context);
    }

    public JProgView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public JProgView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        mHeight = measureHanlder(TYPE_HEIGHT, heightMeasureSpec);
        mWidth = measureHanlder(TYPE_WIDTH, widthMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        initializate();
    }

    private void initializate(){
        mCenter.set(mWidth/2f, mHeight/2f);
        int[] colors = new int[]{mPColor, mSecPColor};
        int[] textColors = new int[]{mTPColor, mSecTPColor};

        mProgLinearGradient = new LinearGradient(mPading, 0, mWidth-mPading, 0, colors, new float[]{0f, 0.001f},
                Shader.TileMode.CLAMP);//渐变区域0-0.001
        mProgTextLinearGradient = new LinearGradient(mPading, 0, mWidth-mPading, 0, textColors, new float[]{0f, 0.001f},
                Shader.TileMode.CLAMP);

        mProgPaint.setShader(mProgLinearGradient);
        mTextPaint.setShader(mProgTextLinearGradient);

        float strokeWidth = mOutPaint.getStrokeWidth();
        mOutRectF.set(mPading+strokeWidth/2, mPading+strokeWidth/2, mWidth-mPading-strokeWidth/2,
                mHeight-mPading-strokeWidth/2);
        mProRectF.set(mPading, mPading, mWidth-mPading, mHeight-mPading);

        if(radio == -1) {
            radio = mProRectF.height()/2;
        }

        mOutPath = new Path();
        mProgPath = new Path();

        mOutPath.addRoundRect(mOutRectF, radio, radio, Path.Direction.CCW);
        mProgPath.addRoundRect(mProRectF, radio, radio, Path.Direction.CCW);

        mBackground = getBackground();

        if(mProg != 0) {
            updateGradient();
        }
        //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
        //            GradientDrawable gradientDrawable = new GradientDrawable();
        //            gradientDrawable.setCornerRadius(radio);
        //            RippleDrawable rippleDrawable = new RippleDrawable(getColorList(), gradientDrawable, null);
        //            setBackground(rippleDrawable);
        //        }
    }

    private void updateGradient(){
        mProgMatrix.setTranslate(( mWidth-2*mPading )*mProg, 0);
        mProgLinearGradient.setLocalMatrix(mProgMatrix);
        mProgTextLinearGradient.setLocalMatrix(mProgMatrix);
        if(mBackground != null) {
            if(mProg>0 && mProg<1) {
                setBackground(null);
            }else {
                setBackground(mBackground);
            }
        }
    }

    private int measureHanlder(int type, int measureSpec){
        int result = type;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY) {
            //match_parent和具体的数值
            result = specSize;
        }else if(specMode == MeasureSpec.AT_MOST) {
            //wrap_content
            //result = Math.min(10, specSize);
            result = autoCalcute(result);
        }else {
            result = autoCalcute(result);
        }
        return result;
    }

    private int autoCalcute(int result){
        getShowMsg();
        if(result == TYPE_WIDTH) {
            int textwidth = Math.round(mTextPaint.measureText(mShow));
            int hpadint = Math.max(getPaddingLeft()+getPaddingRight(), Math.max(26, mHeight));
            result = textwidth+hpadint;
        }else {
            int vpadint = Math.max(getPaddingTop()+getPaddingBottom(), 26);
            int fontHeight = Math.round(getFontHeight(mTextPaint));
            result = fontHeight+vpadint;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        getShowMsg();

        if(mBackground == null) {
            canvas.drawPath(mOutPath, mOutPaint);//画 边框
            canvas.drawPath(mOutPath, mProgPaint);// 画进度
        }

        canvas.drawText(mShow, mCenter.x, mCenter.y+getFontHeight(mTextPaint)/2, mTextPaint);
    }

    private void getShowMsg(){
        mShow = mPercentFormat.format(mProg);
        if(mShowMsgs != null && mShowMsgs.length>=2) {
            if(mProg<=0) {
                mShow = mShowMsgs[0];
            }else if(mProg>=1) {
                mShow = mShowMsgs[1];
            }
        }
    }

    /**
     * 进度为0--1之间
     *
     * @param progress
     */
    public void setProgress(@FloatRange(from = 0, to = 1) float progress){
        mProg = Math.min(Math.max(progress, 0), 1);
        if(mProgLinearGradient != null) {
            updateGradient();
        }
        postInvalidate();
    }

    public float getProgress(){
        return mProg;
    }

    /**
     * 进度颜色包括 进度条颜色 进度背景色
     * 1,进度颜色  2，默认颜色
     *
     * @param colors
     */
    public void setProgColors(@ColorInt @Size(min = 1) int... colors){
        mPColor = colors[0];
        if(colors.length>1) {
            mSecPColor = colors[1];
        }
    }

    public void setProgColorsRes(@NonNull @Size(min = 1) int... colors){
        mPColor = ContextCompat.getColor(getContext(), colors[0]);
        if(colors.length>1) {
            mSecPColor = ContextCompat.getColor(getContext(), colors[1]);
        }
    }

    public void setOutLineColor(@ColorInt int color){
        mOutPaint.setColor(color);
    }

    public void setOutLineColorRes(int color){
        mOutPaint.setColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * 进度颜色包括 进度条颜色 进度背景色
     * 1,进度颜色  2，默认颜色
     *
     * @param colors
     */
    public void setTextColorsRes(@NonNull @Size(min = 1) int... colors){
        mSecTPColor = mTPColor = ContextCompat.getColor(getContext(), colors[0]);
        if(colors.length>1) {
            mSecTPColor = ContextCompat.getColor(getContext(), colors[1]);
        }
    }

    public void setTextColors(@ColorInt @Size(min = 1) int... colors){
        mSecTPColor = mTPColor = colors[0];
        if(colors.length>1) {
            mSecTPColor = colors[1];
        }
    }

    /**
     * 设置 默认的文字 和 进度结束显示的内容
     */
    public void setTextSD(String... msg){
        mShowMsgs = msg;
    }

    public float getRadio(){
        return radio;
    }

    public void setRadio(float radio){
        this.radio = radio;
    }

    public void setTextSize(float textSize){
        mTextPaint.setTextSize(textSize);
    }

}
