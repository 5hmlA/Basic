package com.a4399.miniworld.vp.basic;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.first.a4399.miniworld.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 */
public abstract class AbsListFrgmt extends BaseTitleFrgmt{
    @BindView(R.id.common_recv) public RecyclerView mCommonRecv;
    @BindView(R.id.common_swipe) public SwipeRefreshLayout mSwipeRefreshLayout;
    public List mListData = new ArrayList();
    public MultiTypeAdapter mMultiTypeAdapter;

    @Override
    protected boolean requestNoTitleBar(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){

        View rootView = inflater.inflate(R.layout.common_abslist, container);
        ButterKnife.bind(this, rootView);
        mMultiTypeAdapter = new MultiTypeAdapter();
        mCommonRecv.setLayoutManager(setLayoutManager());
        register2Adapter(mMultiTypeAdapter);
        mCommonRecv.setAdapter(mMultiTypeAdapter);
//        mListData.add(new ItemRecomWorm.ItemRecomWormBuilder().setAdvDesc("测试").build());
//        mListData.add(new ItemRecomWorm.ItemRecomWormBuilder().build());
//        mListData.add(new ItemChangeMoudle("第一个"));
//        mListData.add(new ItemChangeMoudle("第二个"));
//        mMultiTypeAdapter.setItems(mListData);
//        mMultiTypeAdapter.notifyDataSetChanged();
    }

    protected abstract void register2Adapter(MultiTypeAdapter multiTypeAdapter);

    protected abstract RecyclerView.LayoutManager setLayoutManager();

}
