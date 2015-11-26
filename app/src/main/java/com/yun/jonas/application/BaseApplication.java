package com.yun.jonas.application;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Jonas on 2015/11/19.
 */
public class BaseApplication extends Application {
    //初始化 主线程中的上下文
    private static BaseApplication mContext = null;
    //获取到主线程的handler
    private static Handler mMainThreadHandler = null;
    //获取到主线程的looper
    private static Looper mMainThreadLooper = null;
    //获取到主线程
    private static Thread mMainThead = null;
    //获取到主线程的id
    private static int mMainTheadId;
    /**
     * 屏幕宽
     */
    public static int screenW;
    /**
     * 屏幕高
     */
    public static int screenH;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = this;
        this.mMainThreadHandler = new Handler();
        this.mMainThreadLooper = getMainLooper();
        this.mMainThead = Thread.currentThread();
        //android.os.Process.myUid()   获取到用户id
        //android.os.Process.myPid()获取到进程id
        //android.os.Process.myTid()获取到调用线程的id
        this.mMainTheadId = android.os.Process.myTid();

//        CrashPeaker.init().regist(this);

    }

    public static BaseApplication getApplication(){
        return mContext;
    }

    public static Handler getMainThreadHandler(){
        return mMainThreadHandler;
    }

    public static Looper getMainThreadLooper(){
        return mMainThreadLooper;
    }

    public static Thread getMainThread(){
        return mMainThead;
    }

    public static int getMainThreadId(){
        return mMainTheadId;
    }
}
