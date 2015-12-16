package com.yun.jonas.net;

/**
 * Created by Jonas on 2015/11/22.
 */
public abstract class NetResult<Q,T>
{
    public abstract <Q> void onFailure(Q error);

    public abstract <T> void onSucceed(T modle);
}
