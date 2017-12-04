package com.blueprint.helper;

import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blueprint.LibApp;

import static com.blueprint.helper.LogHelper.concat;

/**
 * @another 江祖赟
 * @date 2017/8/10
 * add custom view to activity`s rootview as topview
 */
public class ToastHelper {

    private static final int DEFAULT = -1;
    public static int sMsgColor = DEFAULT;
    public static int sBGColor = DEFAULT;
    private final Toast mToast;
    private int messageColor = DEFAULT;
    private int backgroundColor = DEFAULT;
    private int bgResource = DEFAULT;


    private ToastHelper(){
        mToast = Toast.makeText(LibApp.getContext(), "", Toast.LENGTH_SHORT);
    }

    private static class Inner {
        private static ToastHelper sTostHelper = new ToastHelper();
    }

    public static ToastHelper getSingleton(){
        return Inner.sTostHelper;
    }

    public ToastHelper showToast(Object msg){
        if(CheckHelper.checkObjects(msg)) {
            SpannableString showStr = new SpannableString(msg.toString());
            if(messageColor != DEFAULT) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(messageColor);
                showStr.setSpan(colorSpan, 0, showStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            View view = getToastWindow();
            if(bgResource != DEFAULT) {
                view.setBackgroundResource(bgResource);
            }else if(backgroundColor != DEFAULT) {
                view.setBackgroundColor(backgroundColor);
            }
            mToast.setText(showStr);
            mToast.show();
        }
        return this;
    }

    public ToastHelper showJToast(SpannableString msg){
        return showJToast(msg, -1);
    }

    public ToastHelper showJToast(SpannableString msg, @ColorInt int bgcolor){
        if(CheckHelper.checkObjects(msg)) {
            View view = getToastWindow();
            if(bgcolor != DEFAULT) {
                view.setBackgroundColor(bgcolor);
            }
            mToast.setText(msg);
            mToast.show();
        }
        return this;
    }


    /**
     * 取消吐司显示
     */
    public void cancel(){
        if(mToast != null) {
            mToast.cancel();
        }
    }

    public ToastHelper showToastRes(@StringRes int strRes){
        return showToast(LibApp.findString(strRes));
    }

    public View getToastWindow(){
        return mToast.getView();
    }

    public TextView getToastMsgView(){
        return mToast.getView().findViewById(android.R.id.message);
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor
     *         背景色
     */
    public ToastHelper setBgColor(@ColorInt final int backgroundColor){
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource
     *         背景资源
     */
    public ToastHelper setBgResource(@DrawableRes final int bgResource){
        this.bgResource = bgResource;
        return this;
    }

    /**
     * 设置消息颜色
     *
     * @param messageColor
     *         颜色
     */
    public ToastHelper setMessageColor(@ColorInt final int messageColor){
        this.messageColor = messageColor;
        return this;
    }

    public static void toastRes(@StringRes int resId){
        showShort(LibApp.findString(resId));
    }

    public static Toast showShort(@StringRes int strRes){
        return showShort(LibApp.findString(strRes), DEFAULT);
    }

    public static Toast showShort(CharSequence message){
        return showShort(message, DEFAULT);
    }

    public static Toast showShort(Object... message){
        return showShort(concat(message), DEFAULT);
    }

    public static Toast showShort(CharSequence message, @ColorInt int bgcolor){
        Toast toast = null;
        if(CheckHelper.checkObjects(message)) {
            toast = Toast.makeText(LibApp.getContext(), message, Toast.LENGTH_SHORT);
            if(bgcolor != DEFAULT) {
                toast.getView().setBackgroundColor(bgcolor);
            }
            toast.show();
        }
        return toast;
    }

    public static void showShort(Object message){
        showToast(message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Object message, int duration){
        showToast(message, duration, DEFAULT);
    }

    /**
     * @param message
     * @param duration
     *         1-LONG,0-SHORT
     * @param colors
     *         BGcolor,MSGcolor
     */
    public static TextView showToast(Object message, int duration, @ColorInt Integer... colors){
        if(CheckHelper.checkObjects(message)) {
            CharSequence show = message instanceof CharSequence ? ( (CharSequence)message ) : message.toString();
            Toast toast = Toast.makeText(LibApp.getContext(), show, duration);
            View rootView = toast.getView();
            TextView toastMessage = (TextView)rootView.findViewById(android.R.id.message);
            toastMessage.setGravity(Gravity.CENTER);
            if(CheckHelper.checkArrays(colors)) {
                if(colors[0] != DEFAULT) {
                    rootView.setBackgroundColor(colors[0]);
                }else {
                    if(sBGColor != DEFAULT) {
                        rootView.setBackgroundColor(sBGColor);
                    }
                }
                if(colors.length>1 && colors[1] != DEFAULT) {
                    toastMessage.setTextColor(colors[1]);
                }else {
                    if(sMsgColor != DEFAULT) {
                        toastMessage.setTextColor(sMsgColor);
                    }
                }
            }else {
                if(sBGColor != DEFAULT) {
                    rootView.setBackgroundColor(sBGColor);
                }
                if(sMsgColor != DEFAULT) {
                    toastMessage.setTextColor(sMsgColor);
                }
            }
            toast.show();
            return toastMessage;
        }
        return null;
    }

    public static void show(Toast toast, Object msg){
        if(CheckHelper.checkObjects(msg)) {
            TextView toastMessage = getTextViewFromToast(toast);
            toastMessage.setText(msg.toString());
            toast.show();
        }
    }

    public static Toast configToast(Object message, int duration, @ColorInt Integer... colors){
        if(CheckHelper.checkObjects(message)) {
            CharSequence show = message instanceof CharSequence ? ( (CharSequence)message ) : message.toString();
            Toast toast = Toast.makeText(LibApp.getContext(), show, duration);
            View rootView = toast.getView();
            TextView toastMessage = (TextView)rootView.findViewById(android.R.id.message);
            toastMessage.setGravity(Gravity.CENTER);
            if(CheckHelper.checkArrays(colors)) {
                if(colors[0] != DEFAULT) {
                    toastMessage.setTextColor(colors[0]);
                }else {
                    if(sBGColor != DEFAULT) {
                        toastMessage.setTextColor(sMsgColor);
                    }
                }
                if(colors.length>1 && colors[1] != DEFAULT) {
                    rootView.setBackgroundColor(colors[1]);
                }else {
                    if(sMsgColor != DEFAULT) {
                        rootView.setBackgroundColor(sBGColor);
                    }
                }
            }else {
                if(sBGColor != DEFAULT) {
                    rootView.setBackgroundColor(sBGColor);
                }
                if(sMsgColor != DEFAULT) {
                    toastMessage.setTextColor(sMsgColor);
                }
            }
            return toast;
        }
        return null;
    }

    /**
     * @param message
     * @param duration
     * @param res
     *         BG resource,MSGcolor
     */
    public static void showToast2(Object message, int duration, Integer... res){
        if(CheckHelper.checkObjects(message)) {
            CharSequence show = message instanceof CharSequence ? ( (CharSequence)message ) : message.toString();
            Toast toast = Toast.makeText(LibApp.getContext(), show, duration);
            View rootView = toast.getView();
            TextView toastMessage = (TextView)rootView.findViewById(android.R.id.message);
            toastMessage.setGravity(Gravity.CENTER);
            if(CheckHelper.checkArrays(res)) {
                if(res[0] != DEFAULT) {
                    toastMessage.setTextColor(res[0]);
                }
                if(res.length>1 && res[1] != DEFAULT) {
                    rootView.setBackgroundResource(res[1]);
                }
            }
            toast.show();
        }
    }

    /**
     * 第一个 文字 第二个 背景图
     *
     * @param message
     * @param duration
     * @param res
     * @return
     */
    public static Toast configToast2(Object message, int duration, Integer... res){
        if(CheckHelper.checkObjects(message)) {
            CharSequence show = message instanceof CharSequence ? ( (CharSequence)message ) : message.toString();
            Toast toast = Toast.makeText(LibApp.getContext(), show, duration);
            View rootView = toast.getView();
            TextView toastMessage = (TextView)rootView.findViewById(android.R.id.message);
            toastMessage.setGravity(Gravity.CENTER);
            if(CheckHelper.checkArrays(res)) {
                if(res[0] != DEFAULT) {
                    toastMessage.setTextColor(res[0]);
                }
                if(res.length>1 && res[1] != DEFAULT) {
                    rootView.setBackgroundResource(res[1]);
                }
            }
            return toast;
        }
        return null;
    }

    public static void showToastCustom(Object message, int duration){
        if(CheckHelper.checkObjects(message)) {
            CharSequence show = message instanceof CharSequence ? ( (CharSequence)message ) : message.toString();
            Toast.makeText(LibApp.getContext(), show, duration).show();
        }
    }

    public static void toastShortSafeDebug(Object message){
        if(LibApp.isInDebug()) {
            toastShortSafe(message);
        }else {
            LogHelper.Log_d(message);
        }
    }

    public static void toastLongSafeDebug(Object message){
        if(LibApp.isInDebug()) {
            toastLongSafe(message);
        }
    }

    public static void toastShortSafe(Object message){
        if(CheckHelper.checkObjects(message)) {
            if(Looper.myLooper() == Looper.getMainLooper()) {
                showToast(message, Toast.LENGTH_SHORT);
            }else {
                Looper.prepare();
                showToast(message, Toast.LENGTH_SHORT);
                Looper.loop();
            }
        }
    }

    public static void toastLongSafe(Object message){
        if(CheckHelper.checkObjects(message)) {
            if(Looper.myLooper() == Looper.getMainLooper()) {
                showToast(message, Toast.LENGTH_LONG);
            }else {
                Looper.prepare();
                showToast(message, Toast.LENGTH_LONG);
                Looper.loop();
            }
        }
    }


    public static void toastLongRes(int resId){
        if(CheckHelper.checkObjects(resId)) {
            Toast.makeText(LibApp.getContext(), resId, Toast.LENGTH_LONG).show();
        }
    }


    public static void showLong(Object message){
        showToast(message, Toast.LENGTH_LONG);
    }


    public static void showLongX2(Object message){
        if(CheckHelper.checkObjects(message)) {
            showLong(message);
            showLong(message);
        }
    }


    public static void toastLongX2Res(@StringRes int resId){
        if(CheckHelper.checkObjects(resId)) {
            showLong(resId);
            showLong(resId);
        }
    }


    public static void toastLongX3Res(@StringRes int resId){
        if(CheckHelper.checkObjects(resId)) {
            showLong(resId);
            showLong(resId);
            showLong(resId);
        }
    }


    public static void showLongX3(String message){
        if(CheckHelper.checkObjects(message)) {
            showLong(message);
            showLong(message);
            showLong(message);
        }
    }


    /** Toast一个图片 */
    public static Toast showToastImage(int resID){
        final Toast toast = Toast.makeText(LibApp.getContext(), "", Toast.LENGTH_SHORT);
        View mNextView = toast.getView();
        if(mNextView != null) {
            mNextView.setBackgroundResource(resID);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }


    public static void showCustemToast(View toastview, final int duration, final String text){
        //todo 用toast实现对话框
        //        final Toast toast = Toast.makeText(ctx, text, duration);
        //        View view = RelativeLayout.inflate(ctx, R.layout.toast_layout, null);
        //        TextView mNextView = (TextView) view.findViewById(R.id.toast_name);
        //        toast.setView(view);
        //        mNextView.setText(text);
        //        toast.setGravity(Gravity.CENTER, 0, 0);
        //        toast.show();
    }

    public static void toastErrorMsg(Throwable throwable){
        if(throwable != null && !TextUtils.isEmpty(throwable.getMessage())) {
            ToastHelper.showShort(throwable.getMessage());
        }
    }

    @Nullable
    public static TextView getTextViewFromToast(@Nullable Toast toast){
        if(toast != null) {
            //            return toast.getView().findViewById(com.android.internal.R.id.message);
            return toast.getView().findViewById(android.R.id.message);
        }else {
            return null;
        }
    }
}
