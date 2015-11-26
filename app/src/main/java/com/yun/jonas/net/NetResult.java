package com.yun.jonas.net;

/**
 * Created by Jonas on 2015/11/22.
 */
public abstract class NetResult {
    public abstract<T> void onFailure(T Error);
    public abstract<T> void onSucceed(T Response);
}
