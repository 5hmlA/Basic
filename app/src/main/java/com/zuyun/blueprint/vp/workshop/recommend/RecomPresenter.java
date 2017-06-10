package com.zuyun.blueprint.vp.workshop.recommend;

import com.zuyun.blueprint.data.bean.GanHuoData;
import com.zuyun.blueprint.data.bean.MeiZhi;
import com.zuyun.blueprint.data.netsource.urlapi.GankService;
import com.zuyun.blueprint.data.netsource.urlapi.MeiZhiService;
import com.blueprint.http.HttpResult;
import com.blueprint.http.ServiceFactory;
import com.blueprint.rx.RxUtill;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
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
        Single<HttpResult<List<GanHuoData>>> 福利1 = ServiceFactory.getInstance().createService(GankService.class)
                .getGanHuo("福利", 1);
        Single<HttpResult<List<MeiZhi>>> meizhi = ServiceFactory.getInstance().createService(MeiZhiService.class)
                .getMeizhi(3);
        ServiceFactory.getInstance().createService(GankService.class)
                .getGanHuo("福利", 1)
                .compose(RxUtill.<HttpResult<List<GanHuoData>>>defaultSchedulers_single())
                .subscribe(new Consumer<HttpResult<List<GanHuoData>>>() {
                    @Override
                    public void accept(@NonNull HttpResult<List<GanHuoData>> listHttpResult) throws Exception{
//                        listHttpResult
                        System.out.println("ooo");
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

//        Disposable error = Single
//                .zip(福利1, meizhi, new BiFunction<HttpResult<List<GanHuoData>>,HttpResult<List<MeiZhi>>,List<Object>>() {
//                    @Override
//                    public List<Object> apply(
//                            @NonNull HttpResult<List<GanHuoData>> listHttpResult,
//                            @NonNull HttpResult<List<MeiZhi>> listHttpResult2) throws Exception{
//                        List<Object> objects = new ArrayList<Object>();
//                        objects.add(listHttpResult.results);
//                        objects.add(listHttpResult2);
//
//                        return objects;
//                    }
//                }).compose(RxUtill.<List<Object>>defaultSchedulers_single()).subscribe(new Consumer<List<Object>>() {
//                    @Override
//                    public void accept(@NonNull List<Object> objects) throws Exception{
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception{
//                        System.out.println("error");
//                    }
//                });

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
