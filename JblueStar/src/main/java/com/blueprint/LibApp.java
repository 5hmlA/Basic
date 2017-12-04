package com.blueprint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueprint.crash.CrashWrapper;
import com.blueprint.helper.CheckHelper;
import com.blueprint.helper.LogHelper;
import com.blueprint.helper.SpHelper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;
import static com.blueprint.helper.ToastHelper.toastLongSafeDebug;


public class LibApp {


    private static final String TAG = LibApp.class.getSimpleName();
    @SuppressLint("StaticFieldLeak") protected static Activity sCurrentActivity;
    @SuppressLint("StaticFieldLeak") protected static Context sContext;
    private static boolean sInDebug;
    private static String sBaseUrl = "http://gank.io/api/data/";
    protected static Activity sMainActivity;
    //需要可调
    public static boolean JELLYLIST = false;

    public static CrashWrapper takeCare(Application context, final boolean inDebug){
        sContext = context.getApplicationContext();
        sInDebug = inDebug;
        JELLYLIST = (boolean)new SpHelper("libConfig").get("JELLYLIST", false);
        context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle){
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity){
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity){
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity){

            }

            @Override
            public void onActivityStopped(Activity activity){
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle){

            }

            @Override
            public void onActivityDestroyed(Activity activity){
                sCurrentActivity = sMainActivity;
            }
        });
        context.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onConfigurationChanged(Configuration newConfig){

            }

            @Override
            public void onLowMemory(){

            }

            @Override
            public void onTrimMemory(int level){
                //                TRIM_MEMORY_RUNNING_MODERATE
                //                你的应用正在运行，并且不会被杀死，但设备已经处于低内存状态，并且开始杀死LRU缓存里的内存。
                //                TRIM_MEMORY_RUNNING_LOW
                //                你的应用正在运行，并且不会被杀死，但设备处于内存更低的状态，所以你应该释放无用资源以提高系统性能(直接影响app性能)
                //                TRIM_MEMORY_RUNNING_CRITICAL
                //                你的应用还在运行，但系统已经杀死了LRU缓存里的大多数进程，所以你应该在此时释放所有非关键的资源。如果系统无法回收足够的内存，它会清理掉所有LRU缓存，并且开始杀死之前优先保持的进程，像那些运行着service的。同时，当你的app进程当前被缓存，你可能会从onTrimMemory()收到下面的几种level.
                //                        TRIM_MEMORY_UI_HIDDEN
                //                内存不足，并且该进程的UI已经不可见了。
                //                TRIM_MEMORY_BACKGROUND
                //                系统运行在低内存状态，并且你的进程已经接近LRU列表的,虽然你的app进程还没有很高的被杀死风险，系统可能已经清理LRU里的进程，你应该释放那些容易被恢复的资源，如此可以让你的进程留在缓存里，并且当用户回到app时快速恢复.
                //                        TRIM_MEMORY_MODERATE
                //                系统运行在低内存状态，你的进程在LRU列表中间附近。如果系统变得内存紧张，可能会导致你的进程被杀死。
                //                TRIM_MEMORY_COMPLETE
                //                系统运行在低内存状态，如果系统没有恢复内存，你的进程是首先被杀死的进程之一。你应该释放所有不重要的资源来恢复你的app状态。
                //                因为onTrimMemory()是在API 14里添加的，你可以在老版本里使用onLowMemory()回调，大致跟TRIM_MEMORY_COMPLETE事件相同。
                //                提示：当系统开始杀死LRU缓存里的进程时，尽管它主要从下往上工作，它同时也考虑了哪些进程消耗更多的内存，如果杀死它们，系统会得到更多的可用内存。所以，在LRU整个列表中，你消耗越少的内存，留在列表里的机会就更大。
                if(level<TRIM_MEMORY_UI_HIDDEN) {
                    LibApp.onLowMemory();
                }
            }
        });
        //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2) {
        ////            Android7.0无需FileProvide搞定URI拍照、应用安装问题
        //            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //            StrictMode.setVmPolicy(builder.build());
        //            builder.detectFileUriExposure();
        //        }
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag){
                return inDebug;
            }
        });
        return CrashWrapper.get().setDebug(inDebug);
    }

    public static boolean checkRuntimeFreeMemory(){
        long freeMemory = Runtime.getRuntime().freeMemory();
        long size_kb = freeMemory/1024;
        long size_km = size_kb/1024;
        return size_km==0;
    }

    public static Context getContext(){
        checkContext();
        return sContext;
    }

    public static File getCacheDir(){
        checkContext();
        File externalCacheDir = sContext.getExternalCacheDir();
        if(externalCacheDir == null) {
            externalCacheDir = sContext.getCacheDir();
        }
        return externalCacheDir;
    }

    public static File getAppFileDir(String type){
        checkContext();
        File externalCacheDir = sContext.getExternalFilesDir(type);
        if(externalCacheDir == null) {

            externalCacheDir = Environment.getExternalStorageDirectory();
        }
        return externalCacheDir;
    }

    public static String getPackageName(){
        checkContext();
        return sContext.getPackageName();
    }

    private static void checkContext(){
        if(sContext == null) {
            Log.e(TAG, "需要先调用 takeCare 初始化context");
            throw new RuntimeException("必须 先调用 takeCare 初始化context");
        }
    }

    /**
     * @param textView
     * @param path
     *         字体再asset下的路径
     */
    public static void setTypeFace(TextView textView, String path){
        Typeface mTypeFace = Typeface.createFromAsset(sContext.getAssets(), path);
        textView.setTypeface(mTypeFace);
    }

    /**
     * 加粗汉子
     *
     * @param tv
     */
    public static void blobChinese(TextView tv){
        tv.getPaint().setFakeBoldText(true);
    }

    /** 获取资源 */
    public static Resources findResources(){
        return sContext.getResources();
    }

    /** 获取文字 */
    public static String findString(int resId){
        return findResources().getString(resId);
    }

    /** 获取文字 占位符 */
    public static String findString(int resId, Object... formatArgs){
        return findResources().getString(resId, formatArgs);
    }

    public static int findInteger(int id){
        return findResources().getInteger(id);
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

    /** 获取颜色 */
    public static boolean findBoolen(int resId){
        return sContext.getResources().getBoolean(resId);
    }

    /** 获取颜色 */
    public static int findColor(Context context, int resId){
        return ContextCompat.getColor(context, resId);
    }

    /** 获取颜色状态器 */
    public static ColorStateList findColorStateList(int resId){
        return ContextCompat.getColorStateList(sContext, resId);
    }

    public static void setTextView(View rootView, int id, CharSequence charSequence){
        if(!TextUtils.isEmpty(charSequence)) {
            ( (TextView)rootView.findViewById(id) ).setText(charSequence);
        }
    }

    public static void setTextContent(TextView tv, Object contentObject){
        if(CheckHelper.checkObjects(tv, contentObject)) {
            if(contentObject instanceof Integer) {
                tv.setText(( (Integer)contentObject ));
            }else if(contentObject instanceof CharSequence) {
                tv.setText((CharSequence)contentObject);
            }
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

    public static void setImageSrc(@NonNull View rootView, int id, @DrawableRes int draIds){
        setImageSrc((ImageView)rootView.findViewById(id), ContextCompat.getDrawable(getContext(), draIds));
    }

    public static void setDebugConfig(boolean inDebug){
        sInDebug = inDebug;
    }

    public static boolean isInDebug(){
        return sInDebug;
    }

    /**
     * whether application is in background
     */
    public static boolean isApplicationInBackground(){
        ActivityManager am = (ActivityManager)sContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if(taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if(topActivity != null && !topActivity.getPackageName().equals(sContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String getBaseUrl(){
        return sBaseUrl;
    }

    public static String getChannel(String key){
        //BuildConfig.FLAVOR 就可以了
        PackageManager packageManager = LibApp.getContext().getPackageManager();
        String channel = "debug";
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(LibApp.getPackageName(), GET_META_DATA);
            channel = applicationInfo.metaData.getString(key);
        }catch(PackageManager.NameNotFoundException e) {
            LogHelper.slog_e("getChannel", Log.getStackTraceString(e));
        }
        return channel;
    }

    public static void setBaseUrl(String baseUrl){
        sBaseUrl = baseUrl;
    }

    @Nullable
    public static Activity getCurrentActivity(){
        return sCurrentActivity;
    }

    @Nullable
    public static void setMainActivity(Activity mainActivity){
        sMainActivity = mainActivity;
    }

    @Nullable
    public static void releaseMainActivity(){
        sMainActivity = null;
    }

    @Nullable
    public static void setCurrentActivity(Activity currentActivity){
        sCurrentActivity = currentActivity;
    }

    /**
     * http://blog.csdn.net/liuxu0703/article/details/70145168
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     *
     * @return host activity; or null if not available
     *
     * @from: https://stackoverflow.com/questions/8276634/android-get-hosting-activity-from-a-view/32973351#32973351
     */
    public static Activity getActivityFromView(View view){
        Context context = view.getContext();
        while(context instanceof ContextWrapper) {
            if(context instanceof Activity) {
                return (Activity)context;
            }
            context = ( (ContextWrapper)context ).getBaseContext();
        }
        return sMainActivity;
    }

    public static void toLaunch(){
        if(sMainActivity != null) {
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.addCategory(Intent.CATEGORY_HOME);
            sMainActivity.startActivity(launcherIntent);
        }
    }

    public static Activity getMainActivity(){
        return sMainActivity;
    }

    public static void toggleJellylist(){
        new SpHelper("libConfig").put("JELLYLIST", JELLYLIST = !JELLYLIST);
    }

    public static void onLowMemory(){
        if(sMainActivity != null) {
            //可见但内存不足
            sMainActivity.onLowMemory();
            toastLongSafeDebug("处于低内存环境onTrimMemory");
        }
    }
}
