package com.yun.jonas.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.yun.jonas.BuildConfig;
import com.yun.jonas.utills.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jonas on 2015/11/20.
 */
public class CrashPeaker implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private final static String TAG = "CrashWoodpecker";
    private static boolean mForceHandleByOrigin = false;

    // Default log out time, 7days.
    private final static long LOG_OUT_TIME = 1000*60*60*24*7;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private volatile Thread.UncaughtExceptionHandler mOriginHandler;
    private volatile UncaughtExceptionInterceptor mInterceptor;
    private volatile boolean mCrashing = false;

    private Context mContext;
    private String mVersion;

    /**
     * Install CrashWoodpecker.
     *
     * @return CrashWoodpecker instance.
     */
    public static CrashPeaker init(){
        return init(false);
    }


    /**
     * Install CrashWoodpecker with forceHandleByOrigin param.
     *
     * @param forceHandleByOrigin
     *         whether to force original UncaughtExceptionHandler handle again,
     *         by default false.
     * @return CrashWoodpecker instance.
     */
    public static CrashPeaker init(boolean forceHandleByOrigin){
        mForceHandleByOrigin = forceHandleByOrigin;
        return new CrashPeaker();
    }


    public void regist(Context context){
        mContext = context;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            mVersion = info.versionName+"("+info.versionCode+")";
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    private CrashPeaker(){
        Thread.UncaughtExceptionHandler originHandler = Thread.currentThread().getUncaughtExceptionHandler();

        // check to prevent set again
        if(this != originHandler) {
            mOriginHandler = originHandler;
            Thread.currentThread().setUncaughtExceptionHandler(this);
            // 设置该类为线程默认UncatchException的处理器。
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }


    //    /**
    //     * 当UncaughtException发生时会回调该函数来处理
    //     */
    //    @Override
    //    public void uncaughtException(Thread thread, Throwable ex){
    //
    //        // MBOPApplication app=(MBOPApplication) mainContext;
    //        // app.setNeed2Exit(true);
    //        //异常信息收集
    //        //        collectCrashExceptionInfo(thread, ex);
    //        //应用程序信息收集
    //        //        collectCrashApplicationInfo(app);
    //        //保存错误报告文件到文件。
    //        //        saveCrashInfoToFile(ex);
    //        //MBOPApplication.setCrash(true);
    //        //判断是否为UI线程异常，thread.getId()==1 为UI线程
    //
    //    }

    private boolean handleException(Throwable throwable){
        boolean success = saveToFile(throwable);
        try {
            startCatchActivity(throwable);
            byeByeLittleWood();
        }catch(Exception e) {
            success = false;
        }
        return success;
    }


    private void byeByeLittleWood(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable){
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "获取到错误详情:"+throwable.getMessage());
        }
        // Don't re-enter,  avoid infinite loops if crash-handler crashes.
        if(mCrashing) {
            return;
        }
        mCrashing = true;

        // pass it to interceptor's before method
        UncaughtExceptionInterceptor interceptor = mInterceptor;
        if(interceptor != null && interceptor.onInterceptExceptionBefore(thread, throwable)) {
            return;
        }

        boolean isHandle = handleException(throwable);

        // pass it to interceptor's after method
        if(interceptor != null && interceptor.onInterceptExceptionAfter(thread, throwable)) {
            return;
        }

        if(( mForceHandleByOrigin || !isHandle ) && mOriginHandler != null) {
            mOriginHandler.uncaughtException(thread, throwable);
        }
    }


    /**
     * Set uncaught exception interceptor.
     *
     * @param interceptor
     *         uncaught exception interceptor.
     */
    public void setInterceptor(UncaughtExceptionInterceptor interceptor){
        mInterceptor = interceptor;
    }


    /**
     * Delete outmoded logs.
     */
    public void deleteLogs(){
        deleteLogs(LOG_OUT_TIME);
    }


    /**
     * Delete outmoded logs.
     *
     * @param timeout
     *         outmoded timeout.
     */
    public void deleteLogs(final long timeout){
        final File logDir = new File(getCrashDir());
        if(logDir == null) {
            return;
        }
        try {
            final long currTime = System.currentTimeMillis();
            File[] files = logDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename){
                    File f = new File(dir, filename);
                    return currTime-f.lastModified()>timeout;
                }
            });
            if(files != null) {
                for(File f : files) {
                    FileUtils.delete(f);
                }
            }
        }catch(Exception e) {
            Log.v(TAG, "exception occurs when deleting outmoded logs", e);
        }
    }


    private String getCrashDir(){
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        return rootPath+"/CrashWoodpecker/";
    }


    private void startCatchActivity(Throwable throwable){
        //        String traces = getStackTrace(throwable);
        //        Intent intent = new Intent();
        //        intent.setClass(mContext, CatchActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        String[] strings = traces.split("\n");
        //        String[] newStrings = new String[strings.length];
        //        for(int i = 0; i<strings.length; i++) {
        //            newStrings[i] = strings[i].trim();
        //        }
        //        intent.putExtra(CatchActivity.EXTRA_PACKAGE, mContext.getPackageName());
        //        intent.putExtra(CatchActivity.EXTRA_CRASH_LOGS, newStrings);
        //        mContext.startActivity(intent);
    }


    private String getStackTrace(Throwable throwable){
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return writer.toString();
    }


    private boolean saveToFile(Throwable throwable){
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "保存文本日志");
        }
        String time = mFormatter.format(new Date());
        String fileName = "Crash-"+time+".log";
        String crashDir = getCrashDir();
        String crashPath = crashDir+fileName;

        String androidVersion = Build.VERSION.RELEASE;
        String deviceModel = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;

        File file = new File(crashPath);
        if(file.exists()) {
            file.delete();
        }else {
            try {
                new File(crashDir).mkdirs();
                file.createNewFile();
            }catch(IOException e) {
                return false;
            }
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        }catch(FileNotFoundException e) {
            return false;
        }
        writer.write("Device: "+manufacturer+", "+deviceModel+"\n");
        writer.write("Android Version: "+androidVersion+"\n");
        if(mVersion != null) {
            writer.write("App Version: "+mVersion+"\n");
        }
        writer.write("---------------------\n\n");
        throwable.printStackTrace(writer);
        writer.close();

        return true;
    }


    public interface UncaughtExceptionInterceptor {
        boolean onInterceptExceptionBefore(Thread t, Throwable ex);

        boolean onInterceptExceptionAfter(Thread t, Throwable ex);
    }
}