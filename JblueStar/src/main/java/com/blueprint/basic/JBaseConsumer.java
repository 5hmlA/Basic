package com.blueprint.basic;

import android.app.Activity;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;

/**
 * @another 江祖赟
 * @date 2017/11/23 0023.
 */
public abstract class JBaseConsumer<T> implements Consumer<T> {

    public WeakReference<Activity> mActivityWeakReference;

    public JBaseConsumer(WeakReference<Activity> activityWeakReference){
        mActivityWeakReference = activityWeakReference;
    }

    public JBaseConsumer(){
    }
}
