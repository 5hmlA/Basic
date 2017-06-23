package com.blueprint.basic.common;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @another 江祖赟
 * @date 2017/6/23.
 * 所有presenter的父类 处理一些公共操作
 */
public class PublicPresenter {

    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected void collectDisposables(Disposable disposable){
        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposables(){
        mCompositeDisposable.clear();
    }
}
