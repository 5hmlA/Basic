package com.blueprint.rx;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.blueprint.basic.JBaseView;
import com.blueprint.helper.DialogHelper;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public class RxUtill {
    //    doOnSubscribe()与onStart()类似，均在代码调用时就会回调，但doOnSubscribe()可以通过subscribeOn()操作符改变运行的线程且越在后面运行越早；
    //    doOnSubscribe()后面紧跟subscribeOn()，那么doOnSubscribe()将于subscribeOn()指定的线程保持一致；如果doOnSubscribe()在subscribeOn()之后，他的执行线程得再看情况分析；
    //    doOnSubscribe()如果在observeOn()后（注意：observeon()后没有紧接着再调用subcribeon(),调了也没关系），那么doOnSubscribe的执行线程就是main线程，与observeon()指定的线程没有关系；
    //    如果在observeOn()之前没有调用过subcribeOn()方法，observeOn()之后subscribe面()方法之前调用subcribeOn()方法，那么他会改变整个代码流程中所有调用doOnSubscribe()方法所在的线程，同时也会改变observeOn()方法之前所有操作符所在的线程（有个重要前提：不满足第2点的条件，也就是doOnSubscribe()后面没有调用subscribeOn()方法）；
    //    如果在observeOn()前后都没有调用过subcribeOn()方法，那么整个代码流程中的doOnSubscribe()执行在main线程，与observeOn()指定的线程无关；同时observeOn()之前的操作符也将执行在main线程，observeOn()之后的操作符与observeOn()指定的线程保持一致。

    public static <T> FlowableTransformer<T,T> defaultSchedulers_flow(){
        return new FlowableTransformer<T,T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());

            }
        };
    }

    public static <T> FlowableTransformer<T,T> defaultSchedulers_flow(final JBaseView baseView){
        final WeakReference<JBaseView> jBaseViewWeakReference = new WeakReference<>(baseView);
        return new FlowableTransformer<T,T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream){
                //                doOnSubscribe()如果在observeOn()后（注意：observeon()后没有紧接着再调用subcribeon(),调了也没关系），
                // 那么doOnSubscribe的执行线程就是main线程，与observeon()指定的线程没有关系；
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(
                                    @io.reactivex.annotations.NonNull Subscription subscription) throws Exception{
                                if(jBaseViewWeakReference.get() != null) {
                                    jBaseViewWeakReference.get().showLoading();
                                }
                            }
                        });
                //                        .subscribeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    public static <T> ObservableTransformer<T,T> defaultSchedulers_obser(){
        return new ObservableTransformer<T,T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> ObservableTransformer<T,T> defaultSchedulers_obser2(final Context context, final String message, final boolean cancelable){
        final WeakReference<Dialog> dialogWeakReference = new WeakReference<Dialog>(
                DialogHelper.creageSpinnerProgressDialog(context, message, cancelable));
        return new ObservableTransformer<T,T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(
                                    @io.reactivex.annotations.NonNull Disposable disposable) throws Exception{
                                if(dialogWeakReference.get() != null && !dialogWeakReference.get().isShowing()) {
                                    dialogWeakReference.get().show();
                                }
                            }
                        }).doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception{
                                if(dialogWeakReference.get() != null && dialogWeakReference.get().isShowing()) {
                                    dialogWeakReference.get().dismiss();
                                }
                            }
                        }).doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception{
                                if(dialogWeakReference.get() != null && dialogWeakReference.get().isShowing()) {
                                    dialogWeakReference.get().dismiss();
                                }
                            }
                        });
            }
        };
    }

    public static <T> ObservableTransformer<T,T> defaultSchedulers_obser(final JBaseView baseView){
        return defaultSchedulers_obser2(new WeakReference<>(baseView));
    }

    public static <T> ObservableTransformer<T,T> defaultSchedulers_obser2(final WeakReference<JBaseView> jBaseViewWeakReference){
        return new ObservableTransformer<T,T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception{
                                if(jBaseViewWeakReference.get() != null) {
                                    jBaseViewWeakReference.get().showLoading();
                                }
                            }
                        });
                //                            doOnSubscribe 以及 Observable 的创建操作符总是被其之后最近的 subscribeOn 控制
                //                        .subscribeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> SingleTransformer<T,T> defaultSchedulers_single(){
        return new SingleTransformer<T,T>() {

            @Override
            public SingleSource<T> apply(@NonNull Single<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> SingleTransformer<T,T> defaultSchedulers_single(final JBaseView baseView){
        final WeakReference<JBaseView> jBaseViewWeakReference = new WeakReference<>(baseView);
        return new SingleTransformer<T,T>() {
            @Override
            public SingleSource<T> apply(@NonNull Single<T> upstream){
                return upstream.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception{
                                if(jBaseViewWeakReference.get() != null) {
                                    jBaseViewWeakReference.get().showLoading();
                                }
                            }
                        });
                //                .subscribeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> FlowableTransformer<T,T> all_io_flow(){
        return new FlowableTransformer<T,T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream){
                return upstream.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> ObservableTransformer<T,T> all_io_obser(){
        return new ObservableTransformer<T,T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream){
                return upstream.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> SingleTransformer<T,T> all_io_single(){
        return new SingleTransformer<T,T>() {
            @Override
            public SingleSource<T> apply(@NonNull Single<T> upstream){
                return upstream.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static void dispose(Disposable disposable){
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
