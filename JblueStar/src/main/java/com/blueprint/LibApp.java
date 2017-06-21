package com.blueprint;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;


public class LibApp {

    private static final String TAG = LibApp.class.getSimpleName();

    private static Context sContext;
    private static boolean sInDebug;

    public static void init(Context context,boolean inDebug){
        sInDebug = inDebug;
        sContext = context.getApplicationContext();
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return sInDebug;
            }
        });

    }

    public static Context getContext(){
        checkContext();
        return sContext;
    }

    public static String getPackageName(){
        checkContext();
        return sContext.getPackageName();
    }

    private static void checkContext(){
        if(sContext == null) {
            Log.e(TAG, "需要先调用init初始化context");
            throw new RuntimeException("必须 先调用init初始化context");
        }
    }


    /** 获取资源 */
    public static Resources findResources(){

        return getContext().getResources();
    }

    /** 获取文字 */
    public static String findString(int resId){
        return findResources().getString(resId);
    }

    /** 获取文字数组 */
    public static String[] findStringArray(int resId){
        return findResources().getStringArray(resId);
    }

    /** 获取dimen */
    public static int findDimens(int resId){
        return findResources().getDimensionPixelSize(resId);
    }

    /** 获取drawable */
    public static Drawable findDrawable(int resId){
        return ContextCompat.getDrawable(sContext, resId);
    }

    /** 获取颜色 */
    public static int findColor(int resId){
        return ContextCompat.getColor(sContext, resId);
    }

    /** 获取颜色选择器 */
    public static ColorStateList findColorStateList(int resId){
        return ContextCompat.getColorStateList(sContext, resId);
    }

    public static void setTextView(View rootView, int id, CharSequence charSequence){
        if(!TextUtils.isEmpty(charSequence)) {
            ( (TextView)rootView.findViewById(id) ).setText(charSequence);
        }
    }

    public static void setTextView(TextView tv, CharSequence charSequence){
        if(tv != null && !TextUtils.isEmpty(charSequence)) {
            tv.setText(charSequence);
        }
    }

    public static void setImageSrc(ImageView iv, Drawable drawable){
        if(iv != null && drawable != null) {
            iv.setImageDrawable(drawable);
        }
    }

    public static void setTextView(@NonNull View rootView, int id, int resStr){
        setTextView((TextView)rootView.findViewById(id), findString(resStr));
    }

    public static void setImageSrc(@NonNull View rootView, int id, int resStr){
        setImageSrc((ImageView)rootView.findViewById(id), ContextCompat.getDrawable(getContext(), resStr));
    }

    public static void setDebugConfig(boolean inDebug){
        sInDebug = inDebug;
    }

    public static boolean isInDebug(){
        return sInDebug;
    }

    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context
     * @return if application is in background return true, otherwise return false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
