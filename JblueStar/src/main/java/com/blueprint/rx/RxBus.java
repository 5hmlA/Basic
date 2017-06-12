package com.blueprint.rx;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

import static android.R.attr.key;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public class RxBus {
    private final FlowableProcessor<Object> mBus;
    private final SparseArrayCompat<CompositeDisposable> mCompositeDisposable = new SparseArrayCompat<>();


    private RxBus(){
        mBus = PublishProcessor.create().toSerialized();
    }

    private static class Holder {
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance(){
        return Holder.instance;
    }

    public void post(@NonNull Object obj){
         mBus.onNext(obj);
    }

    public <T> Flowable<T> register(Class<T> clz){
        return mBus.ofType(clz);
    }

    public <T> Flowable<T> registerStiky(Class<T> clz){
        return mBus.ofType(clz).share();
    }

    public void unregisterAll(){
        //解除注册
        mBus.onComplete();
    }

    public boolean hasSubscribers(){
        return mBus.hasSubscribers();
    }

    public RxBus putDisposable(int key, Disposable value){
        CompositeDisposable compositeDisposable = mCompositeDisposable.get(key);
        if(compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            mCompositeDisposable.put(key, compositeDisposable);
        }
        compositeDisposable.add(value);
        return this;
    }

    public RxBus putDisposable(Class clazz, Disposable value){
        CompositeDisposable compositeDisposable = mCompositeDisposable.get(clazz.hashCode());
        if(compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            mCompositeDisposable.put(key, compositeDisposable);
        }
        compositeDisposable.add(value);
        return this;
    }

    public RxBus dispose(int key){
        CompositeDisposable compositeDisposable = mCompositeDisposable.get(key);
        if(compositeDisposable != null) {
            compositeDisposable.clear();
        }
        return this;
    }

    public RxBus dispose(Class clazz){
        CompositeDisposable compositeDisposable = mCompositeDisposable.get(clazz.hashCode());
        if(compositeDisposable != null) {
            compositeDisposable.clear();
        }
        return this;
    }
}
