package com.blueprint.helper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.blueprint.LibApp;
import com.blueprint.R;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;
import static android.app.ProgressDialog.STYLE_SPINNER;

public class DialogHelper2 {
    private final Dialog mDialog;

    private DialogHelper2(){
        mDialog = new Dialog(LibApp.getCurrentActivity(), R.style.transDialogStyle);
        //        mDialog = new Dialog(activity.getApplicationContext(), R.style.sugDialogStyle);
        //        // 全局弹出提示对话框，不需要使用activity上下文
        //        if (PhoneHelper.isMIUI()) {
        //            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST) ;//小米手机使用次方式
        //        }else {
        //            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) ;
        //        }
    }

    public static DialogHelper2 create(){
        return new DialogHelper2();
    }

    public DialogHelper2 customDialog(int dialoglayout){
        mDialog.setContentView(dialoglayout);
        mDialog.setCanceledOnTouchOutside(true);
        return this;
    }

    public Dialog getDialog(){
        return mDialog;
    }


    public DialogHelper2 setOnclickListener(int ids, View.OnClickListener listener){
        if(null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setOnClickListener(listener);
        }
        return this;
    }

    public DialogHelper2 setText(int ids, String content){
        if(null != mDialog.findViewById(ids)) {
            ( (TextView)mDialog.findViewById(ids) ).setText(content);
        }
        return this;
    }

    public DialogHelper2 setText(int ids, int strRes){
        if(null != mDialog.findViewById(ids)) {
            ( (TextView)mDialog.findViewById(ids) ).setText(strRes);
        }
        return this;
    }

    public DialogHelper2 setVisibility(int ids, int visibility){
        if(null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setVisibility(visibility);
        }
        return this;
    }

    public DialogHelper2 show(){
        mDialog.show();
        return this;
    }

    public DialogHelper2 dismiss(){
        mDialog.dismiss();
        return this;
    }


    public DialogHelper2 asIOSBottomStyle(){
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(-1, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return this;
    }

    /**
     * 距离顶部的距离 在dialog布局里面设置margtop就可以了
     *
     * @return
     */
    public DialogHelper2 asTopOptionStyle(){
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP);
        window.setLayout(-1, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return this;
    }

    public static Dialog createMsgDialog(String message, boolean cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(LibApp.getCurrentActivity());
        builder.setCancelable(cancelable).setMessage(message);
        return builder.create();
    }

    public static ProgressDialog creageHorizontalProgressDialog(String message, boolean cancelable){
        return creageProgressDialog(message, cancelable, STYLE_HORIZONTAL);
    }

    public static ProgressDialog creageSpinnerProgressDialog(String message, boolean cancelable){
        return creageProgressDialog(message, cancelable, STYLE_SPINNER);
    }

    public static ProgressDialog creageProgressDialog(String message, boolean cancelable, int progressStyle){
        ProgressDialog progressDialog = new ProgressDialog(LibApp.getCurrentActivity());
        if(!TextUtils.isEmpty(message)) {
            progressDialog.setMessage(message);
        }
        progressDialog.setCanceledOnTouchOutside(cancelable);
        if(progressStyle != 0) {
            progressDialog.setProgressStyle(progressStyle);
        }
        return progressDialog;
    }

}
