package com.zuyun.blueprint.vp.workshop.recommend;

import com.blueprint.http.HttpResult;
import com.blueprint.http.ServiceFactory;
import com.blueprint.rx.RxUtill;
import com.orhanobut.logger.Logger;
import com.zuyun.blueprint.data.bean.GanHuoData;
import com.zuyun.blueprint.data.netsource.urlapi.GankService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class RecomPresenter implements RecomContract.IRecoPresenter {

    private RecomContract.IRecomView mView;
    private CompositeDisposable mCompositeDisposable;

    public RecomPresenter(RecomContract.IRecomView view){
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe(){
        Logger.d("工坊推荐tab --- sunscribe");
        mView.showLoading();
        loadData();
    }

    private void loadData(){
        //加载 推荐广告
        //判断是否需要更新下载
        //加载 精选
        //加载 插卡模块
        ServiceFactory.getInstance().createService(GankService.class).getGanHuo("福利", 1)
                .compose(RxUtill.<HttpResult<List<GanHuoData>>>defaultSchedulers_single())
                .subscribe(new Consumer<HttpResult<List<GanHuoData>>>() {
                    @Override
                    public void accept(@NonNull HttpResult<List<GanHuoData>> listHttpResult) throws Exception{
                        List<String> urls = new ArrayList<String>();
                        for(GanHuoData result : listHttpResult.results) {
                            urls.add(result.getUrl());
                        }

                        mView.addLoopImageHolder(urls);
                        mView.showSucceed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception{
                        System.out.println("eeee");
                    }
                });

        loadLoopImage();
    }

    @Override
    public void unsubscribe(){
        Logger.d("RecomPresenter - unsubscribe");
        mCompositeDisposable.clear();
        mView = null;
    }

    @Override
    public void loadLoopImage(){

    }

    @Override
    public void checkUpdate(){

    }

    @Override
    public void loadHot(){

    }

    @Override
    public void loadModule(){

    }
}
