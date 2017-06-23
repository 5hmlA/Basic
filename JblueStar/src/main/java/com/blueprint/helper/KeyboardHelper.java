package com.blueprint.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.blueprint.LibApp;

/**
 * 通过hook监听系统键盘显示
 * https://github.com/pqpo/InputMethodHolder
 * http://blog.csdn.net/ccpat/article/details/46717573
 */
public final class KeyboardHelper {

    /**
     * showSoftInput(view,flag)
     * 第一个参数中view必须是EditText，或者EditText的子类，如果是其他类型的View，如Button，TextView等，showSoftInput()方法不起作用。
     * 第一个参数中的view必须是可以获取焦点的（即view.isFocusable()返回true），如果不能获取焦点，则showSoftInput()方法不起作用。EditText默认是可获取焦点的，所以此条件一般都可以满足。如果不满足，可以通过view.setFocusable(true);将其设置为可获取焦点的View。
     * 第一个参数中的view当前必须已经获取到焦点（即view.isFocused()返回true），如果当前焦点不在该view上，则showSoftInput()方法不起作用。虽然EditText默认是可获取焦点的，但由于一个布局中可能会有多个控件可以获取焦点，焦点位置不一定会恰好在EditText上，所以此条件不一定满足。为了让showSoftInput()可以起作用，必须在之前showSoftInput()前先通过view.requestFocus();获取焦点。然后再执行showSoftInput()。
     * 第一个参数中的view必须是可见的，即view.getVisibility()等于View.VISIBLE，如果view是不可见的，无论view.getVisibility()是View.INVISIBLE还是View.GONE，showSoftInput()方法都不起作用。如果view是不可见的，可以先通过view.setVisibility(View.VISIBLE)将其设置为可见。
     *
     * @param view
     */
    public static void showKeyboard(View view){
        if(view != null) {
            view.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(Activity activity){
        if(activity != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                activity.getCurrentFocus().clearFocus();
            }
        }
    }

    /**
     * 切换键盘显示与否状态
     */
    public static void toggleKeyboard(){
        InputMethodManager imm = (InputMethodManager)LibApp.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm == null) {
            return;
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(View view){
        if(view != null) {
            InputMethodManager imm = (InputMethodManager)view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public static boolean isKeyBoardActive(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();//isOpen若返回true，则表示输入法打开
    }
}