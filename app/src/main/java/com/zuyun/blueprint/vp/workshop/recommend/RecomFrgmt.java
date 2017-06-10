package com.zuyun.blueprint.vp.workshop.recommend;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zuyun.blueprint.Constants;
import com.zuyun.blueprint.vp.basic.BaseTitleFrgmt;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.ItemChangeMoudleBinder;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.ItemLoopImageBinder;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.ItemRecomWormBinder;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemChangeMoudle;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemLoopImage;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemRecomWorm;
import com.zuyun.blueprint.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jonas.jlayout.MultiStateLayout;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 */
public class RecomFrgmt extends BaseTitleFrgmt implements RecomContract.IRecomView {
    @BindView(R.id.common_recv) RecyclerView mCommonRecv;
    private RecomPresenter mRecomPresenter;
    private List mListData;
    private MultiTypeAdapter mMultiTypeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean requestNoTitleBar(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){

        View rootView = inflater.inflate(R.layout.common_recv_only, container);
        ButterKnife.bind(this, rootView);
        mBasePresenter = mRecomPresenter = new RecomPresenter(this);

        mMultiTypeAdapter = new MultiTypeAdapter();
        mCommonRecv.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mMultiTypeAdapter.register(ItemRecomWorm.class, new ItemRecomWormBinder());
        mMultiTypeAdapter.register(ItemChangeMoudle.class, new ItemChangeMoudleBinder(mRecomPresenter));
        mMultiTypeAdapter.register(ItemLoopImage.class, new ItemLoopImageBinder());
        mCommonRecv.setAdapter(mMultiTypeAdapter);
        mListData = new ArrayList();
        mListData.add(new ItemRecomWorm.ItemRecomWormBuilder().setAdvDesc("测试").build());
        mListData.add(new ItemRecomWorm.ItemRecomWormBuilder().build());
        mListData.add(new ItemChangeMoudle("第一个"));
        mListData.add(new ItemChangeMoudle("第二个"));
        mMultiTypeAdapter.setItems(mListData);
        mMultiTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading(){

    }

    @Override
    public void showSucceed(){
        mListData.add(1, new ItemChangeMoudle("第x个"));
        mMultiTypeAdapter.setItems(mListData);
        mMultiTypeAdapter.notifyItemInserted(1);
        mMultiStateLayout.showStateLayout(MultiStateLayout.LayoutState.STATE_EXCEPT);
    }

    @Override
    public void showError(Constants.ErrorCode code){

    }

    @Override
    public void addLoopImageHolder(List<String> urls){
        // 如果第一个holder不是loopimage就添加
        mListData.add(0, new ItemLoopImage(urls));
        mMultiTypeAdapter.setItems(mListData);
        mMultiTypeAdapter.notifyItemInserted(0);
        mCommonRecv.smoothScrollToPosition(0);

    }

    @Override
    public void addUpdateHolder(boolean addHolder){

    }

    @Override
    public void addHotHolder(){

    }

    @Override
    public void addUnstableHolder(){

    }

    @Override
    public boolean getUserVisibleHint(){
        return super.getUserVisibleHint();
    }

}
