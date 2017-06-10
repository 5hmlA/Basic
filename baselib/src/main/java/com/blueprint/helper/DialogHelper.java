package com.blueprint.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

public class DialogHelper {
    private final Dialog mDialog;

    private DialogHelper(Activity activity) {
        mDialog = new Dialog(activity, com.blueprint.R.style.transDialogStyle);
//        mDialog = new Dialog(activity.getApplicationContext(), R.style.sugDialogStyle);
//        // 全局弹出提示对话框，不需要使用activity上下文
//        if (PhoneHelper.isMIUI()) {
//            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST) ;//小米手机使用次方式
//        }else {
//            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT) ;
//        }
    }

    public static DialogHelper create(Activity activity) {
        return new DialogHelper(activity);
    }

    public DialogHelper customDialog(int dialoglayout) {
        mDialog.setContentView(dialoglayout);
        mDialog.setCanceledOnTouchOutside(true);
        return this;
    }

    public Dialog getDialog() {
        return mDialog;
    }


    public DialogHelper setOnclickListener(int ids, View.OnClickListener listener) {
        if (null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setOnClickListener(listener);
        }
        return this;
    }

    public DialogHelper setText(int ids, String content) {
        if (null != mDialog.findViewById(ids)) {
            ((TextView) mDialog.findViewById(ids)).setText(content);
        }
        return this;
    }

    public DialogHelper setText(int ids, int strRes) {
        if (null != mDialog.findViewById(ids)) {
            ((TextView) mDialog.findViewById(ids)).setText(strRes);
        }
        return this;
    }

    public DialogHelper setVisibility(int ids, int visibility) {
        if (null != mDialog.findViewById(ids)) {
            mDialog.findViewById(ids).setVisibility(visibility);
        }
        return this;
    }

    public DialogHelper show() {
        mDialog.show();
        return this;
    }

    public DialogHelper dismiss() {
        mDialog.dismiss();
        return this;
    }


}
