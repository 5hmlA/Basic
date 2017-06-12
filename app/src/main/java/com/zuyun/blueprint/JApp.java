package com.zuyun.blueprint;

import android.app.Application;
import android.content.Context;

import com.blueprint.LibApp;
import com.squareup.leakcanary.LeakCanary;

public class JApp extends Application {


    @Override
    public void onCreate(){
        super.onCreate();
        LibApp.init(this, BuildConfig.DEBUG);
        if(LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

    }

    public static Context getInstance(){
        return LibApp.getContext();
    }

}
