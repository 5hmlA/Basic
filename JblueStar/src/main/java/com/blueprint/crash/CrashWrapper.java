package com.blueprint.crash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

import com.blueprint.JSettingCenter;
import com.blueprint.LibApp;
import com.blueprint.crash.ui.CatchActivity;
import com.blueprint.crash.ui.PatchDialogActivity;
import com.blueprint.helper.LogHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static com.blueprint.crash.CrashLog.saveCrashToSdcard;
import static com.blueprint.crash.CrashWrapper.CrashHandleMode.SHOW_CRASH_DIALG;
import static com.blueprint.crash.CrashWrapper.CrashHandleMode.SHOW_CRASH_PAGE;
import static com.blueprint.helper.RegexHelper.SPLIT_DOT;

/**
 * Created by wanjian on 2017/2/14.
 * https://github.com/android-notes/Cockroach/blob/master/%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md
 */

public final class CrashWrapper implements Thread.UncaughtExceptionHandler {
    private final static DateFormat FORMATTER = DateFormat.getDateInstance();

    private volatile UncaughtExceptionInterceptor interceptor;
    private volatile boolean crashing;
    private UncaughtExceptionInterceptor sExceptionInterceptor;
    private Thread.UncaughtExceptionHandler mOrignUncaughtExceptionHandler;
    private boolean sInstalled = false;//标记位，避免重复安装卸载
    private static Context sContext;
    /* For highlight */
    private ArrayList<String> keys = new ArrayList<>();
    //    private String version;
    //    private int handleMode = CrashHandleMode.SHOW_CRASH_DIALG;
    private int handleMode = CrashHandleMode.SHOW_CRASH_PAGE;
    private String patchDialogMessage;
    private String patchDialogUrlToOpen;
    private boolean mIsDebug = true;
    private boolean mAutoSaveLog;
    private boolean mAutoRestart = true;
    public static boolean catchDefaultUncaughtException = true;

    public static void restartApp(){
        Log.d("CrashWrapper", "=========Restart=======");
        Intent intent = sContext.getPackageManager().getLaunchIntentForPackage(sContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private CrashWrapper(){
        crashing = false;
        initContextResources();
    }

    public static CrashWrapper get(){
        sContext = LibApp.getContext();
        return new CrashWrapper();
    }

    /**
     * 当主线程或子线程抛出异常时会调用exceptionHandler.handlerException(Thread thread, Throwable throwable)
     * exceptionHandler.handlerException可能运行在非UI线程中。
     * 若设置了Thread.setDefaultUncaughtExceptionHandler则可能无法捕获子线程异常。
     *
     * @param exceptionInterceptor
     */
    public synchronized void watching(UncaughtExceptionInterceptor exceptionInterceptor){
        if(sInstalled || !catchDefaultUncaughtException) {
            //不捕获异常关闭
            return;
        }
        sInstalled = true;
        sExceptionInterceptor = exceptionInterceptor;
        if(!mIsDebug) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run(){
                    while(true) {
                        try {
                            Looper.loop();
                        }catch(Throwable e) {
                            if(isInstanceOfNullQuitCockroachException(e)) {
                                //终结循环
                                return;
                            }
                            crashing = true;
                            crashLogSaveCheck(e);
                            if(isInstanceOfSQLiteException(e)) {
                                //如果是数据库 异常就强力清除应用数据 退出应用
                                JSettingCenter.strongClearAppCache();
                            }else if(isInstanceOfOutOfMemoryError(e) || isInstanceOfNullPointerException(e)) {
                                restartApp();
                            }else {
                                if(sExceptionInterceptor != null) {
                                    sExceptionInterceptor.onhandlerException(Looper.getMainLooper().getThread(), e);
                                }
                            }
                        }
                    }
                }
            });
        }
        mOrignUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static boolean isInstanceOfSQLiteException(Throwable e){
        if(e instanceof SQLiteException) {
            return true;
        }else if(e.getCause() != null) {
            return isInstanceOfSQLiteException(e.getCause());
        }else {
            return false;
        }
    }

    public static boolean isInstanceOfOutOfMemoryError(Throwable e){
        if(e instanceof OutOfMemoryError) {
            return true;
        }else if(e.getCause() != null) {
            return isInstanceOfSQLiteException(e.getCause());
        }else {
            return false;
        }
    }

    public static boolean isInstanceOfNullPointerException(Throwable e){
        if(e instanceof NullPointerException) {
            return true;
        }else if(e.getCause() != null) {
            return isInstanceOfSQLiteException(e.getCause());
        }else {
            return false;
        }
    }

    public static boolean isInstanceOfNullQuitCockroachException(Throwable e){
        if(e instanceof QuitCockroachException) {
            return true;
        }else if(e.getCause() != null) {
            return isInstanceOfSQLiteException(e.getCause());
        }else {
            return false;
        }
    }

    public synchronized void giveup(){
        if(!sInstalled) {
            return;
        }
        sInstalled = false;
        sExceptionInterceptor = null;
        //卸载后恢复默认的异常处理逻辑，否则主线程再次抛出异常后将导致ANR，并且无法捕获到异常位置
        Thread.setDefaultUncaughtExceptionHandler(this);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run(){
                throw new QuitCockroachException("Quit CrashWrapper.....");//主线程抛出异常，迫使 while (true) {}结束
            }
        });

    }

    @Override
    public synchronized void uncaughtException(Thread thread, Throwable throwable){
        // Don't re-enter, avoid infinite loops if crash-handler crashes.
        if(crashing) {
            Log.e("CrashWrapper", "re-enter uncaughtException: \n"+Log.getStackTraceString(throwable));
            if(mAutoRestart) {
                restartApp();
            }else {
                System.exit(0);
            }
            return;
        }
        crashing = true;

        if(sExceptionInterceptor != null && !isDebug()) {
            sExceptionInterceptor.onhandlerException(thread, throwable);
        }
        LogHelper.Log_e(throwable);
        crashLogSaveCheck(throwable);
        boolean success = handleException(throwable);
        if(!success && mOrignUncaughtExceptionHandler != null) {
            if(mAutoRestart) {
                restartApp();
            }else {
                mOrignUncaughtExceptionHandler.uncaughtException(thread, throwable);
            }
        }else {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void crashLogSaveCheck(Throwable throwable){
        if(mAutoSaveLog) {
            saveCrashToSdcard(sContext, throwable);
        }
    }

    private boolean handleException(Throwable throwable){
        try {
            if(handleMode == SHOW_CRASH_PAGE) {
                crashCatchActivity(throwable);
            }else if(handleMode == SHOW_CRASH_DIALG) {
                patchDialogMessage = getTopCause(throwable);
                crashCatchDialog();
            }
        }catch(Exception e) {
            return false;
        }
        return true;
    }

    public static String getTopCause(Throwable throwable){
        String topCauseMessage = Log.getStackTraceString(throwable);
        if(TextUtils.isEmpty(topCauseMessage)) {
            while(throwable.getCause() != null) {
                throwable = throwable.getCause();
                topCauseMessage = Log.getStackTraceString(throwable);
            }
        }
        return topCauseMessage;
    }

    public static Throwable getTopCauseThrowable(Throwable throwable){
        String topCauseMessage = Log.getStackTraceString(throwable);
        if(TextUtils.isEmpty(topCauseMessage)) {
            while(throwable.getCause() != null) {
                throwable = throwable.getCause();
                topCauseMessage = Log.getStackTraceString(throwable);
            }
        }
        return throwable;
    }

    private void crashCatchDialog(){
        Intent intent = PatchDialogActivity
                .newIntent(sContext, getApplicationName(sContext), patchDialogMessage, patchDialogUrlToOpen);
        sContext.startActivity(intent);
    }

    private void crashCatchActivity(Throwable throwable){
        String traces = getStackTrace(throwable);
        Intent intent = new Intent();
        intent.setClass(sContext, CatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String[] strings = traces.split("\n");
        String[] logs = new String[strings.length];
        for(int i = 0; i<strings.length; i++) {
            logs[i] = strings[i].trim();
        }
        intent.putStringArrayListExtra(CatchActivity.EXTRA_HIGHLIGHT_KEYS, keys);
        intent.putExtra(CatchActivity.EXTRA_APPLICATION_NAME, getApplicationName(sContext));
        intent.putExtra(CatchActivity.EXTRA_CRASH_LOGS, logs);
        intent.putExtra(CatchActivity.EXTRA_CRASH_4_LOGCAT, Log.getStackTraceString(throwable));
        sContext.startActivity(intent);
    }

    public static String getStackTrace(Throwable throwable){
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return writer.toString();
    }

    public static SpannableString formatStr(String text, int style){
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(sContext, style), 0, text.length(), 0);
        return spannableString;
    }

    private String getApplicationName(Context context){
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        String name = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
            name = (String)packageManager.getApplicationLabel(applicationInfo);
        }catch(final PackageManager.NameNotFoundException e) {
            String[] packages = context.getPackageName().split(SPLIT_DOT);
            name = packages[packages.length-1];
        }
        return name;
    }

    private void initContextResources(){
        this.keys.add(this.sContext.getPackageName());
        //        try {
        //            PackageInfo info = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
        //            version = info.versionName+"("+info.versionCode+")";
        //        }catch(Exception e) {
        //            throw new RuntimeException(e);
        //        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_CRASH_PAGE, SHOW_CRASH_DIALG})
    public @interface CrashHandleMode {
        public static final int SHOW_CRASH_PAGE = 1;
        public static final int SHOW_CRASH_DIALG = 2;
    }

    /**
     * For setting more highlight keys except package name
     *
     * @param keys
     *         highlight keys except package name
     * @return itself
     */
    public CrashWrapper withKeys(final String... keys){
        this.keys.addAll(Arrays.asList(keys));
        return this;
    }

    public int getHandleMode(){
        return handleMode;
    }

    public CrashWrapper setHandleMode(@CrashHandleMode int handleMode){
        this.handleMode = handleMode;
        return this;
    }

    public String getPatchDialogMessage(){
        return patchDialogMessage;
    }

    public CrashWrapper setPatchDialogMessage(String patchDialogMessage){
        this.patchDialogMessage = patchDialogMessage;
        return this;
    }

    public String getPatchDialogUrlToOpen(){
        return patchDialogUrlToOpen;
    }

    public CrashWrapper setPatchDialogUrlToOpen(String patchDialogUrlToOpen){
        this.patchDialogUrlToOpen = patchDialogUrlToOpen;
        return this;
    }

    public boolean isDebug(){
        return mIsDebug;
    }

    public CrashWrapper setDebug(boolean debug){
        mIsDebug = debug;
        return this;
    }

    public boolean isAutoSaveLog(){
        return mAutoSaveLog;
    }

    public CrashWrapper setAutoSaveLog(boolean autoSaveLog){
        mAutoSaveLog = autoSaveLog;
        return this;
    }

    public CrashWrapper setAutoRestart(boolean autoRestart){
        mAutoRestart = autoRestart;
        return this;
    }
}
