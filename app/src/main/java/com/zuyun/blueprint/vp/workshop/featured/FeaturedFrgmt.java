package com.zuyun.blueprint.vp.workshop.featured;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.blueprint.adapter.RecyclerHolder;
import com.zuyun.blueprint.vp.basic.AbsListFrgmt;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.ItemChangeMoudleBinder;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.ItemRecomWormBinder;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemChangeMoudle;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemRecomWorm;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [精选]
 */
public class FeaturedFrgmt extends AbsListFrgmt {
    @Override
    protected RecyclerView.LayoutManager setLayoutManager(){
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected void register2Adapter(MultiTypeAdapter multiTypeAdapter){
        multiTypeAdapter.register(ItemChangeMoudle.class, new ItemChangeMoudleBinder(null));
        multiTypeAdapter.register(ItemRecomWorm.class, new ItemRecomWormBinder());
    }

    @Override
    public void onRefresh(){
        mMultiStateLayout.postDelayed(new Runnable() {
            @Override
            public void run(){
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, 2000);
    }

    @Override
    public void onLoadingMore(){

        System.out.println("oooooooo");
    }

    @Override
    public int setPageSize(){
        return 2;
    }

    @Override
    protected RecyclerHolder setLoadingHolder(ViewGroup parent){
        return super.setLoadingHolder(parent);
    }

    @Override
    protected boolean setEnableUpMore(){
        return false;
    }

    @Override
    public void firstUserVisibile(){
        super.firstUserVisibile();
        mMultiStateLayout.postDelayed(new Runnable() {
            @Override
            public void run(){
                mMultiStateLayout.showStateSucceed();
                mRecvAdapter.addItem(new ItemChangeMoudle("第二个"), 0);
                mRecvAdapter.addItem(new ItemChangeMoudle("第二44444"), 0);
                mRecvAdapter.addItem(new ItemRecomWorm.ItemRecomWormBuilder().setAdvDesc("测试").build(), 0);
                mRecvAdapter.addItem(new ItemRecomWorm.ItemRecomWormBuilder().build(), 0);
                mRecvAdapter.addItem(new ItemChangeMoudle("第一个"), 0);
                mRecvAdapter.addItem(new ItemChangeMoudle("第二个"), 0);
            }
        }, 2000);
    }
}
