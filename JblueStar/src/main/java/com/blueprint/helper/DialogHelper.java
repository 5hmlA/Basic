package com.blueprint.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blueprint.R;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;
import static android.app.ProgressDialog.STYLE_SPINNER;

public class DialogHelper {

    public static class JDialogFragment extends DialogFragment{

    }
    private Dialog mDialog;

    private DialogHelper(){
    }

    private DialogHelper(Activity activity){
        mDialog = new Dialog(activity, com.blueprint.R.style.transDialogStyle);
        //        mDialog = new Dialog(activity.getApplicationContext(), R.style.sugDialogStyle);
        //        // 全局弹出提示对话框，不需要使用activity上下文
        //        if (PhoneHelper.isMIUI()) {
        //            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST) ;//小米手机使用次方式
        //        }else {
        //            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) ;
        //        }
    }

    public static DialogHelper create(Activity activity){
        return new DialogHelper(activity);
    }

    public DialogHelper customDialog(int dialoglayout){
        mDialog.setContentView(dialoglayout);
        mDialog.setCanceledOnTouchOutside(true);
        //dialog弹出软键盘
        //mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        return this;
    }

    public Dialog getDialog(){
        return mDialog;
    }


    public DialogHelper setOnclickListener(int ids, View.OnClickListener listener){
        if(null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setOnClickListener(listener);
        }
        return this;
    }

    public DialogHelper shieldBackPress(){
        return shieldBackPress(true);
    }

    public DialogHelper shieldBackPress(final boolean b){
        shieldBackPress(mDialog, b);
        return this;
    }


    public DialogHelper setText(int ids, CharSequence content){
        if(null != mDialog.findViewById(ids)) {
            ( (TextView)mDialog.findViewById(ids) ).setText(content);
        }
        return this;
    }

    public DialogHelper setText(int ids, int strRes){
        if(null != mDialog.findViewById(ids)) {
            ( (TextView)mDialog.findViewById(ids) ).setText(strRes);
        }
        return this;
    }

    public DialogHelper setVisibility(int ids, int visibility){
        if(null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setVisibility(visibility);
        }
        return this;
    }

    public DialogHelper show(){
        mDialog.show();
        return this;
    }

    public DialogHelper toggle(){
        if(mDialog.isShowing()) {
            mDialog.dismiss();
        }else {
            mDialog.show();
        }
        return this;
    }

    public DialogHelper dismiss(){
        mDialog.dismiss();
        return this;
    }

    public DialogHelper asIOSBottomStyle(){
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        //默认有左右pading  必须在setLayout上面
        window.getDecorView().setPadding(0,0,0,0);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return this;
    }

    public static DialogHelper asIOSBottomStyle(DialogHelper dialogHelper){
        Window window = dialogHelper.getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        //默认有左右pading
        window.getDecorView().setPadding(0,0,0,0);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return dialogHelper;
    }

    public static Dialog asIOSBottomStyle(Dialog dialog){
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //默认有左右pading
        window.getDecorView().setPadding(0,0,0,0);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return dialog;
    }

    public DialogHelper autoShowSoftkeyboard(){
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return this;
    }

    public DialogHelper ediTextDialog(){
        mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return this;
    }

    public DialogHelper setSoftInputMode(int mode){
        mDialog.getWindow().setSoftInputMode(mode);
        return this;
    }

    /**
     * 距离顶部的距离 在dialog布局里面设置margtop就可以了
     *
     * @return
     */
    public DialogHelper asTopOptionStyle(){
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP);
        window.setLayout(-1, -2);
        window.setWindowAnimations(R.style.iosBottomAniStyle);
        return this;
    }

    public static Dialog createMsgDialog(Context context, CharSequence message, boolean cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(cancelable).setMessage(message);
        return builder.create();
    }

    public static ProgressDialog creageHorizontalProgressDialog(Context context, CharSequence message, boolean cancelable){
        return creageProgressDialog(context, message, cancelable, STYLE_HORIZONTAL);
    }

    public static ProgressDialog creageSpinnerProgressDialog(Context context, CharSequence message, boolean cancelable){
        return creageProgressDialog(context, message, cancelable, STYLE_SPINNER);
    }

    public static ProgressDialog creageProgressDialog(Context context, CharSequence message, boolean cancelable, int progressStyle){
        ProgressDialog progressDialog = new ProgressDialog(context);
        if(!TextUtils.isEmpty(message)) {
            progressDialog.setMessage(message);
        }
        progressDialog.setCanceledOnTouchOutside(cancelable);
        if(progressStyle != 0) {
            progressDialog.setProgressStyle(progressStyle);
        }
        return progressDialog;
    }

    public <T> T getView(int ids){
        return (T)mDialog.findViewById(ids);
    }

    public CharSequence getText(int ids){
        return ( (TextView)mDialog.findViewById(ids) ).getText();
    }

    public DialogHelper setCanceledOnTouchOutside(boolean b){
        if(mDialog != null) {
            mDialog.setCanceledOnTouchOutside(b);
        }
        return this;
    }

    public static void shieldBackPress(Dialog dialog, final boolean b){
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent){
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return b;
                }else {
                    return false;
                }
            }

        });
    }
}
