package com.blueprint.helper.spannable;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.BaseMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:54
 */
public class ClickMovementMethod extends BaseMovementMethod  implements View.OnTouchListener {
    public final String TAG = ClickMovementMethod.class.getSimpleName();
    private final static int DEFAULT_COLOR_ID = Color.TRANSPARENT;
    private final static int DEFAULT_CLICKABLEA_COLOR_ID = Color.GRAY;
    /**整个textView的背景色*/
    private int textViewBgColor;
    /**点击部分文字时部分文字的背景色*/
    private int clickableSpanBgClor;

    private BackgroundColorSpan mBgSpan;
    private ClickableSpan[] mClickLinks;
    private boolean isPassToTv = true;
    /**
     * spanClick点击 span字符串的起点下标
     */
    public static int mClickSpanStart;
    /**
     * spanClick点击 span字符串的终点下标
     */
    public static int mClickSpanEnd;

    /**
     * true：响应textview的点击事件， false：响应设置的clickableSpan事件
     */
    public boolean isPassToTv() {
        return isPassToTv;
    }
    private void setPassToTv(boolean isPassToTv){
        this.isPassToTv = isPassToTv;
    }

    private static ClickMovementMethod sInstance;

    public static ClickMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickMovementMethod();
        }
        return sInstance;
    }


    public ClickMovementMethod(){
        this.textViewBgColor = DEFAULT_COLOR_ID;
        this.clickableSpanBgClor = DEFAULT_CLICKABLEA_COLOR_ID;
    }

    /**
     *
     * @param clickableSpanBgClor  点击选中部分时的背景色
     */
    public ClickMovementMethod(int clickableSpanBgClor){
        this.clickableSpanBgClor = clickableSpanBgClor;
        this.textViewBgColor = DEFAULT_COLOR_ID;
    }

    /**
     *
     * @param clickableSpanBgClor 点击选中部分时的背景色
     * @param textViewBgColor 整个textView点击时的背景色
     */
    public ClickMovementMethod(int clickableSpanBgClor, int textViewBgColor){
        this.textViewBgColor = textViewBgColor;
        this.clickableSpanBgClor = clickableSpanBgClor;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            mClickLinks = buffer.getSpans(off, off, ClickableSpan.class);
            if(mClickLinks.length > 0){
                // 点击的是Span区域，不要把点击事件传递
                setPassToTv(false);
                mClickSpanStart = buffer.getSpanStart(mClickLinks[0]);
                mClickSpanEnd = buffer.getSpanEnd(mClickLinks[0]);
                Selection.setSelection(buffer, mClickSpanStart, mClickSpanEnd);
                //设置点击区域的背景色
                mBgSpan = new BackgroundColorSpan(clickableSpanBgClor);
                buffer.setSpan(mBgSpan, mClickSpanStart, mClickSpanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                setPassToTv(true);
                // textview选中效果
//                widget.setBackgroundColor(textViewBgColor);
            }

        }else if(action == MotionEvent.ACTION_UP){
            if(mClickLinks.length > 0){
                mClickLinks[0].onClick(widget);
                if(mBgSpan != null){//移除点击时设置的背景span
                    buffer.removeSpan(mBgSpan);
                }
            }else{

            }
            Selection.removeSelection(buffer);
//            widget.setBackgroundColor(Color.TRANSPARENT);
        }else if(action == MotionEvent.ACTION_MOVE){
            //这种情况不用做处理
        }else{
            if(mBgSpan != null){//移除点击时设置的背景span
                buffer.removeSpan(mBgSpan);
            }
//            widget.setBackgroundColor(Color.TRANSPARENT);
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TextView widget = (TextView) v;
        Object text = widget.getText();
        if (text instanceof Spanned) {
            Spanned buffer = (Spanned) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        // Selection only works on Spannable text. In our case setSelection doesn't work on spanned text
                        //Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                    }
                    return true;
                }
            }

        }

        return false;
    }
}
