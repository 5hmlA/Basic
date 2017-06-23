package com.blueprint.basic.common;

import com.blueprint.rx.RxUtill;

import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * @another 江祖赟
 * @date 2017/6/23.
 */
public class GeneralListPresenter extends PublicPresenter implements GeneralListContract.Presenter {

    GeneralListContract.View mView;

    public GeneralListPresenter(GeneralListContract.View view){
        mView = view;
    }

    @Override
    public void subscribe(){
        collectDisposables(Flowable.just(1).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception{
                mView.showLoading();
            }
        }).subscribeOn(
                AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).compose(RxUtill.defaultSchedulers_flow()).subscribe(new
                                                                                                                                                           Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception{
                mView.showSucceed(null);
            }
        }));
    }

    @Override
    public void unsubscribe(){
        clearDisposables();
    }

    @Override
    public void search(String key){
        collectDisposables(Flowable.just(1).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception{
                mView.showLoading();
            }
        }).subscribeOn(
                AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception{
                mView.showSucceed(null);
            }
        }));
    }

    @Override
    public void up2LoadMoreData(List containerData){
        collectDisposables(Flowable.just(1).delay(2, TimeUnit.SECONDS).compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception{
                mView.onMoreLoad(null);
            }
        }));
    }

    @Override
    public void down2RefreshData(List containerData){
        collectDisposables(Flowable.just(1).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception{
                mView.showLoading();
            }
        }).subscribeOn(
                AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception{
                mView.showSucceed(null);
            }
        }));
    }
}
