package com.blueprint.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.blueprint.helper.KeyboardHelper;

import static com.blueprint.helper.DpHelper.dp2px;

/**
 * @another 江祖赟
 * @date 2017/6/23.
 * 搜索框 增加 x 删除
 * http://blog.csdn.net/ccpat/article/details/46652921
 * http://www.jianshu.com/p/306482e17080 (Android爬坑之旅：软键盘挡住输入框问题的终极解决方案webview)
 */
@SuppressLint("AppCompatCustomView")
public class JEditText extends EditText {

    private RectF mDelRectf = new RectF(0, 0, 0, 0);
    private RectF mDelClickArea = new RectF(0, 0, 0, 0);
    private int mDelColor = Color.RED;
    private float mDelWidth = dp2px(1.6f);
    private float mDelRightPading = dp2px(14);
    private boolean mShowClickArea;
    /**
     * 删除图标 的高度 是 搜索框高度的几分之几
     */
    private float mDelHeightRadio = 7f/20;
    private Paint mDelPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(mDelColor);
            setStrokeWidth(mDelWidth);
            setStyle(Style.STROKE);//画线
        }
    };
    private int mW;
    private int mH;
    private OnContentClearListener mClearListener;

    public JEditText(Context context){
        super(context);
    }

    public JEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public JEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        mW = w;
        mH = h;
        super.onSizeChanged(w, h, oldw, oldh);
        setSingleLine();
        calcuteDelArea();
    }

    private void calcuteDelArea(){
        float delW = mH*mDelHeightRadio;
        mDelRectf.left = mW-mDelRightPading-delW;
        mDelRectf.top = mH/2f-delW/2;
        mDelRectf.right = mW-mDelRightPading;
        mDelRectf.bottom = mH/2f+delW/2;
        mDelClickArea = new RectF(mDelRectf.left-dp2px(4), 0, mW-dp2px(2), mH);
        //不让输入内容 超过删除图标  超过长度会滚动 导致x也跟着滚动 bug
        setPadding(getPaddingLeft(), getPaddingTop(), (int)( mW-mDelRectf.left+dp2px(8) ), getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(!TextUtils.isEmpty(getText())) {
            canvas.save();
            canvas.restore();
            //画X
            if(mShowClickArea) {
                canvas.drawRect(mDelClickArea, mDelPaint);
            }
            canvas.drawLine(mDelRectf.left, mDelRectf.top, mDelRectf.right, mDelRectf.bottom, mDelPaint);
            canvas.drawLine(mDelRectf.right, mDelRectf.top, mDelRectf.left, mDelRectf.bottom, mDelPaint);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if(mDelClickArea.contains(event.getX(), event.getY())) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mDelClickArea.contains(event.getX(), event.getY())) {
                    setText("");
                    clearFocus();//不清除focus会呼出 长按出现的黏贴悬浮窗选项
                    //呼出键盘
                    KeyboardHelper.showKeyboard(this);
                    if(mClearListener != null) {
                        mClearListener.onClear();
                    }
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void setOnContentClearListener(OnContentClearListener clearListener){

        mClearListener = clearListener;
    }

    public interface OnContentClearListener {
        void onClear();
    }

    public JEditText setDelColor(int delColor){
        mDelColor = delColor;
        mDelPaint.setColor(mDelColor);
        return this;
    }

    public JEditText setDelWidth(float delWidth){
        mDelWidth = delWidth;
        mDelPaint.setStrokeWidth(mDelWidth);
        return this;
    }

    public JEditText setDelRightPading(float delRightPading){
        mDelRightPading = delRightPading;
        return this;
    }

    public JEditText setShowClickArea(boolean showClickArea){
        mShowClickArea = showClickArea;
        return this;
    }

    public JEditText setDelHeightRadio(float delHeightRadio){
        mDelHeightRadio = delHeightRadio;
        return this;
    }
}
