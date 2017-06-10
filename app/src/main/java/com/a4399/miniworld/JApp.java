package com.a4399.miniworld;

import android.app.Application;
import android.content.Context;

import com.baselib.LibApp;
import com.first.a4399.miniworld.BuildConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import cn.m4399.operate.OperateCenter;

public class JApp extends Application {

    private OperateCenter mOpeCenter;

    @Override
    public void onCreate(){
        super.onCreate();
        LibApp.init(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    public static Context getInstance(){
        return LibApp.getContext();
    }

}
