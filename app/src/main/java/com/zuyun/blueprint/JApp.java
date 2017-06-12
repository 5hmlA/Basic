package com.zuyun.blueprint;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.blueprint.LibApp;
import com.squareup.leakcanary.LeakCanary;

public class JApp extends Application implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onCreate(){
        super.onCreate();
        LibApp.init(this, BuildConfig.DEBUG);
        if(LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        registerActivityLifecycleCallbacks(this);


    }

    public static Context getInstance(){
        return LibApp.getContext();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState){
    }

    @Override
    public void onActivityStarted(Activity activity){

    }

    @Override
    public void onActivityResumed(Activity activity){

    }

    @Override
    public void onActivityPaused(Activity activity){

    }

    @Override
    public void onActivityStopped(Activity activity){

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState){

    }

    @Override
    public void onActivityDestroyed(Activity activity){
    }
}
