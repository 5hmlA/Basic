package com.blueprint.basic.common;

import android.text.TextUtils;

import com.blueprint.rx.RxUtill;

import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.blueprint.helper.LogHelper.Log_d;

/**
 * @another 江祖赟
 * @date 2017/6/23.
 */
public class GeneralListPresenter<IT> extends PublicPresenter implements GeneralListContract.Presenter<IT> {
    //不final 第一页可能为其他 0
    public static int FIRST_PAGE = 1;
    protected GeneralListContract.View mListView;
    protected WeakReference<GeneralListContract.View> mWeakListView;
    protected String mLastSearchKey;
    public static String CURRENT_SEARCH_KEY;
    public long mCurrentPage = FIRST_PAGE;
    public Object mFrom;

    public GeneralListPresenter(GeneralListContract.View listView){
        mListView = listView;
        mWeakListView = new WeakReference<GeneralListContract.View>(listView);
    }

    public GeneralListPresenter(){
    }

    protected  <T> ObservableTransformer<T,T> up2loadmoreSchedulers(){
        if(mCurrentPage == FIRST_PAGE) {
            return RxUtill.<T>defaultSchedulers_obser(mListView);
        }else {
            return RxUtill.<T>defaultSchedulers_obser();
        }
    }

    @Override
    public void subscribe(Object fromParam){
		if(mFrom != fromParam) {
            mCurrentPage = FIRST_PAGE;
        }
        mFrom = fromParam;
        collectDisposables(Flowable.just(1).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception{
                mListView.showLoading();
            }
        }).delay(1, TimeUnit.SECONDS)
                .compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception{
                        mListView.showSucceed(null);
                    }
                }));
    }

    @Override
    public void unsubscribe(){
        clearDisposables();
        CURRENT_SEARCH_KEY = "";
        mListView = null;
    }

    @Override
    public void search(String key){
        CURRENT_SEARCH_KEY = key;
        if(!key.equals(mLastSearchKey)) {
			mCurrentPage = FIRST_PAGE;
            toSearchFromService(key);
        }else {
            theSameSearchKey(key);
        }
        mLastSearchKey = key;
    }

    protected void theSameSearchKey(String key){
        toSearchFromService(key);
    }

    protected void toSearchFromService(final String key){
        Log_d("搜索关键字："+key+"--------"+mListView.toString());
        collectDisposables(Flowable.just(1).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception{
                mListView.showLoading();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).delay(1, TimeUnit.SECONDS)
                .compose(RxUtill.defaultSchedulers_flow()).subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception{
                        //搜索成功 保存关键字
                        mLastSearchKey = key;
                        mListView.showSucceed(null);
                    }
                }));
    }

    @Override
    public void up2LoadMoreData(List<IT>  containerData){
        ++mCurrentPage;//建议用索引
        retryUp2LoadMoreData(containerData);
    }

    @Override
    public void retryUp2LoadMoreData(List<IT>  containerData){
        if(TextUtils.isEmpty(CURRENT_SEARCH_KEY)) {
            //关键字为空 非搜索
            subscribe(mFrom);
        }else {
            toSearchFromService(CURRENT_SEARCH_KEY);
        }
    }

    @Override
    public void down2RefreshData(List<IT>  containerData){
        mCurrentPage = FIRST_PAGE;
        if(TextUtils.isEmpty(CURRENT_SEARCH_KEY)) {
            //关键字为空 非搜索
            subscribe(mFrom);
        }else {
            toSearchFromService(CURRENT_SEARCH_KEY);
        }
    }

    @Override
    public void down2RefreshData(Object  fromParam){
        mCurrentPage = FIRST_PAGE;
        mFrom = fromParam;
        if(TextUtils.isEmpty(CURRENT_SEARCH_KEY)) {
            subscribe(mFrom);
        }else {
            toSearchFromService(CURRENT_SEARCH_KEY);
        }
    }

}
