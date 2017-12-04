package com.blueprint.basic.common;

import com.blueprint.basic.JBaseView;
import com.blueprint.helper.LogHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @another 江祖赟
 * @date 2017/6/23.
 * 所有presenter的父类 处理一些公共操作
 */
public class PublicPresenter {
    public JBaseView mBaseView;

    public PublicPresenter(){
    }

    public PublicPresenter(JBaseView baseView){
        mBaseView = baseView;
    }

    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected void collectDisposables(Disposable disposable){
        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposables(){
        LogHelper.Log_d("before-clearDisposables()-: "+mCompositeDisposable.size());
        mCompositeDisposable.clear();
        LogHelper.Log_d("after-clearDisposables()-: "+mCompositeDisposable.size());
    }
}
