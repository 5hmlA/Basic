package com.blueprint.helper;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.helper.spannable.JverScalSpan;
import com.blueprint.helper.spannable.OnSpanClickListener;
import com.blueprint.helper.spannable.SpannableClickable;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import april.yun.other.JTabStyleDelegate;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.blueprint.LibApp.findColor;
import static com.blueprint.LibApp.findDimens;
import static yun.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;

/**
 * @another 江祖赟
 * @date 2017/7/3.
 */
public class UIhelper {

    public static final int CLICK_INTERVAL = 200;
    public static final int TAG_RECVOVERALL_2MOVE = 0X10000001;

    /**
     * 无边框 圆角
     *
     * @param textView
     * @param bgColor
     * @param textColor
     */
    public static GradientDrawable RoundBgText(TextView textView, int bgColor, int textColor){
        return RoundBgText(textView, Integer.MAX_VALUE, 0, bgColor, textColor);
    }

    /**
     * 无边框 圆角
     *
     * @param textView
     * @param bgColor
     *         背景颜色
     * @param textColor
     *         文字颜色
     */
    public static void RoundBgTextRes(TextView textView, int bgColor, int textColor){
        RoundBgText(textView, Integer.MAX_VALUE, 0, findColor(bgColor), findColor(textColor));
    }

    /**
     * 边框2 文字边框颜色一样
     *
     * @param textView
     * @param color
     *         文字颜色
     */
    public static GradientDrawable RoundBgText(TextView textView, int color){
        return RoundBgText(textView, Float.MAX_VALUE, color);
    }

    /**
     * 边框2 文字边框颜色一样
     *
     * @param textView
     * @param color
     *         文字颜色
     */
    public static GradientDrawable RoundBgTextRes(TextView textView, int color){
        return RoundBgText(textView, Float.MAX_VALUE, findColor(color));
    }

    /**
     * 边框为2
     *
     * @param textView
     * @param cornerRadius
     *         圆角半斤
     * @param color
     *         文字颜色
     */
    public static GradientDrawable RoundBgText(TextView textView, float cornerRadius, int color){
        return RoundBgText(textView, cornerRadius, 1, color, color);
    }

    /**
     * @param textView
     * @param strokeWidth
     * @param strokeColor
     * @param textColor
     */
    public static GradientDrawable RoundBgText(TextView textView, float cornerRadius, int strokeWidth, int strokeColor, int textColor){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(cornerRadius);
        drawable.setColor(Color.TRANSPARENT);//To shange the solid color gd
        if(strokeWidth == 0) {
            drawable.setColor(strokeColor);
        }else {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        textView.setTextColor(textColor);
        textView.setBackground(drawable); //不同标签不同背景颜色
        return drawable;
    }

    public static JTabStyleDelegate initTabStrip(JTabStyleDelegate tabStyleDelegate){
        //        2，拿TabStyleDelegate
        //        3, 用TabStyleDelegate设置属性
        tabStyleDelegate.setShouldExpand(false)
                //也可以直接传字符串的颜色，第一个颜色表示checked状态的颜色第二个表示normal状态
                .setTextColor(findColor(R.color.colorPrimary), findColor(R.color.j_gray999))
                .setTabTextSize(findDimens(R.dimen.tab_top_textsize)).setTabPadding(findDimens(R.dimen.tab_pading))
                .setDividerPadding(0)//tab之间分割线 的上下pading
                .setTabPadding(0).setUnderlineHeight(0)//底部横线的高度
                .setIndicatorHeight(findDimens(R.dimen.tab_indicator_height))
                .setUnderlineHeight(findDimens(R.dimen.tab_underline_height))
                .setUnderlineColor(Color.parseColor("#e6e6e6")).setIndicatorColor(findColor(R.color.colorPrimary));
        return tabStyleDelegate;
    }

    public static PopupWindow getDynamicDelPopWindow(Context context, View.OnClickListener listener){
        //评论删除
        PopupWindow delWindow = new PopupWindow(context);
        //        View contentView = layout2View(R.layout.dynamic_home_popdel, null);
        //        View delView = contentView.findViewById(R.id.dynamic_home_del);
        //        delView.setOnClickListener(listener);
        //        delWindow.setContentView(contentView);
        //        delWindow.setWidth(dp2pxCeilInt(78));
        //        delWindow.setHeight(dp2pxCeilInt(37));
        //        delWindow.setOutsideTouchable(true);
        //        delWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        delView.setTag(delWindow);
        //        delWindow.getContentView().setTag(delView);
        return delWindow;
    }

    public static DialogHelper getCommonDelDialog(Activity activity, String content, View.OnClickListener listener){

        return DialogHelper.create(activity).customDialog(R.layout.dialog_jblu_common_msg)
                .setText(R.id.dialog_jblu_common_content, content).setOnclickListener(R.id.dialog_cancel, listener)
                .setOnclickListener(R.id.dialog_confirm, listener);
    }

    public static SpannableString clickSpanAbleString(CharSequence spanString, int textColor, final OnSpanClickListener spanClickListener){
        SpannableString subjectSpanText = new SpannableString(spanString);
        subjectSpanText.setSpan(new SpannableClickable(textColor) {
            @Override
            public void onClick(View widget){
                // TODO add check if widget instanceof TextView
                TextView tv = (TextView)widget;
                // TODO add check if tv.getText() instanceof Spanned
                Spanned s = (Spanned)tv.getText();
                int start = s.getSpanStart(this);
                int end = s.getSpanEnd(this);
                Log.d("clickSpan", "onSpanClick ["+s.subSequence(start, end)+"]");
                spanClickListener.onSpanClick(s.subSequence(start, end));
            }
        }, 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public static SpannableString clickDelSpanAbleString(String spanString, int textColor, int delDrawRes, final OnSpanClickListener spanClickListener){

        JverScalSpan jVerticalImageSpan = new JverScalSpan(delDrawRes);
        SpannableString subjectSpanText = new SpannableString(spanString+"-");
        subjectSpanText.setSpan(jVerticalImageSpan, spanString.length(), spanString.length()+1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subjectSpanText.setSpan(new SpannableClickable(textColor) {
            @Override
            public void onClick(View widget){
                // TODO add check if widget instanceof TextView
                TextView tv = (TextView)widget;
                // TODO add check if tv.getText() instanceof Spanned
                Spanned s = (Spanned)tv.getText();
                int start = s.getSpanStart(this);
                int end = s.getSpanEnd(this);
                Log.d("clickSpan", "onSpanClick ["+s.subSequence(start, end)+"]");
                spanClickListener.onSpanClick(s.subSequence(start, end));
            }
        }, 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public static int getDestYinWindos(final RecyclerView mCommonRecv, final int keyboardHeight){
        int[] recvlocation = new int[2];
        mCommonRecv.getLocationInWindow(recvlocation);
        int visiHeight = mCommonRecv.computeVerticalScrollExtent();
        //        return recvlocation[1]+visiHeight-keyboardHeight;
        return DpHelper.getScreenHeight()-keyboardHeight;
    }

    public static void toMoveRecycleView(final View view, final RecyclerView mCommonRecv, final int destYposition){
        view.post(new Runnable() {
            @Override
            public void run(){
                if(view != null) {
                    int[] outlocation = new int[2];
                    view.getLocationInWindow(outlocation);
                    int mRecycleMoved = outlocation[1]+view.getMeasuredHeight()-destYposition;
                    int offset = mCommonRecv.computeVerticalScrollOffset();
                    int range = mCommonRecv.computeVerticalScrollRange()-mCommonRecv.computeVerticalScrollExtent();
                    int canMoved = range-offset;
                    if(mRecycleMoved<=0 || ( mRecycleMoved>0 && mRecycleMoved<canMoved )) {
                        mCommonRecv.smoothScrollBy(0, mRecycleMoved);
                    }else {
                        if(mCommonRecv.canScrollVertically(1)) {
                            //可以向上滚动
                            mCommonRecv.scrollBy(0, mRecycleMoved);//滚出全部
                            //全部显示后 重新计算 需要滚动的距离
                            offset = mCommonRecv.computeVerticalScrollOffset();
                            range = mCommonRecv.computeVerticalScrollRange()-mCommonRecv.computeVerticalScrollExtent();
                            canMoved = range-offset;
                            view.getLocationInWindow(outlocation);
                            mRecycleMoved = outlocation[1]+view.getMeasuredHeight()-destYposition;
                        }
                        int recyoverall_2move = mRecycleMoved-Math.abs(canMoved);//无法滚动的距离
                        if(recyoverall_2move>0) {
                            ( (View)mCommonRecv.getParent() ).scrollBy(0, recyoverall_2move);
                            mCommonRecv.setTag(TAG_RECVOVERALL_2MOVE, recyoverall_2move);
                        }
                    }
                }
            }
        });
    }

    public static void moveRecvRestore(RecyclerView mCommonRecv){
        if(mCommonRecv != null && mCommonRecv.getParent() != null && mCommonRecv
                .getTag(TAG_RECVOVERALL_2MOVE) != null && ( (int)mCommonRecv.getTag(TAG_RECVOVERALL_2MOVE) )>0) {
            ( (View)mCommonRecv.getParent() ).scrollBy(0, -( (int)mCommonRecv.getTag(TAG_RECVOVERALL_2MOVE) ));
            mCommonRecv.setTag(TAG_RECVOVERALL_2MOVE, 0);
        }
    }

    public static void recyclerViewScroll2Top(RecyclerView mCommonRecv){
        recyclerViewScroll2Position(mCommonRecv, 0);
    }

    public static void recyclerViewScroll2Position(RecyclerView mCommonRecv, int position){
        if(mCommonRecv.computeVerticalScrollOffset()>9*DpHelper.getScreenHeight()) {
            mCommonRecv.scrollToPosition(position);
        }else {
            mCommonRecv.smoothScrollToPosition(position);
        }
    }

    public static Observable<Integer> clicksObserver(View view, final int times){
        Observable<Object> share = RxView.clicks(view).share();
        //debounce 连续事件中相差指定时间 发出事件 （一个时间如果在指定时间内没另一个事件就发送事件）
        return share.buffer(share.debounce(CLICK_INTERVAL, TimeUnit.MILLISECONDS))
                .map(new Function<List<Object>,Integer>() {
                    @Override
                    public Integer apply(List<Object> objects) throws Exception{
                        return objects.size();
                    }
                }).filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception{
                        return integer == times;
                    }
                });
    }

    public static void devAboutPage(View view, final int times){
        final long[] mHints = new long[times];
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //将mHints数组内的所有元素左移一个位置
                System.arraycopy(mHints, 1, mHints, 0, mHints.length-1);
                //获得当前系统已经启动的时间
                mHints[mHints.length-1] = SystemClock.uptimeMillis();
                if(mHints[times-1]-mHints[0]<=CLICK_INTERVAL*times) {
                    IntentHelper.openUrl("https://github.com/ZuYun");
                }
            }
        });
    }

    public static void devAboutPage2(View view, final int times){
        if(LibApp.JELLYLIST) {
            final long[] mHints = new long[times];
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    //将mHints数组内的所有元素左移一个位置
                    System.arraycopy(mHints, 1, mHints, 0, mHints.length-1);
                    //获得当前系统已经启动的时间
                    mHints[mHints.length-1] = SystemClock.uptimeMillis();
                    if(mHints[times-1]-mHints[0]<=CLICK_INTERVAL*times) {
                        IntentHelper.openUrl("https://github.com/ZuYun");
                    }
                }
            });
        }
    }

    public static void wrapper(View view){
        if(LibApp.JELLYLIST) {
            Damping.wrapper(view).configDirection(LinearLayout.VERTICAL);
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    public static void customScaleInLayoutTransition(ViewGroup viewGroup){
        LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
        if(layoutTransition == null) {
            layoutTransition = new LayoutTransition();
            viewGroup.setLayoutTransition(layoutTransition);
        }
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX",  0f, 1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY",  0f, 1f);
        // Adding
        ObjectAnimator customAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(viewGroup, pvhScaleX, pvhScaleY).
                setDuration(layoutTransition.getDuration(LayoutTransition.CHANGE_APPEARING));
        customAppearingAnim.setInterpolator(new OvershootInterpolator());
        // Removing
        ObjectAnimator customDisappearingAnim = ObjectAnimator.ofFloat(viewGroup, "rotationX", 0f, 90f).
                setDuration(layoutTransition.getDuration(LayoutTransition.DISAPPEARING));
        layoutTransition.setAnimator(LayoutTransition.APPEARING, customAppearingAnim);
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, customDisappearingAnim);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String textViewHintFromSp(SpHelper spHelper, String key, TextView textView){
        String value = spHelper.get(key, "").toString();
        if(CheckHelper.checkStrings(value)) {
            textView.setHint(value);
        }
        return value;
    }

    /**
     * showImage 接口
     * @return
     */
    public static void clickableImgInWebView(WebView webView){
        webView.loadUrl("javascript:(" + readImageJs() + ")()");
    }
    public static String readImageJs(){
        return FileHelper.readTextfromAsset("imgjs");//文件内有注释 会有异常
        //        return "function()  \n"+"{  \n"+"    var imgs = document.getElementsByTagName(\"img\");  \n"+"    for(var i = 0; i < imgs.length; i++)  \n"+"    {  \n"+"        imgs[i].onclick = function()  \n"+"        {   \n"+"            window._dsbridge.showImage(this.src);  \n"+"        }  \n"+"    }  \n"+"}";
    }

}
