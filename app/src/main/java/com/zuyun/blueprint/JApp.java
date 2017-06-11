package com.zuyun.blueprint;

import android.app.Application;
import android.content.Context;

import com.blueprint.LibApp;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

public class JApp extends Application {


    @Override
    public void onCreate(){
        super.onCreate();
        LibApp.init(this);
        LibApp.setDebugConfig(BuildConfig.DEBUG);
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
