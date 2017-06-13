package com.zuyun.blueprint.vp.basic;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.adapter.LoadMoreWrapperAdapter;
import com.blueprint.adapter.RecyclerHolder;
import com.zuyun.blueprint.R;

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
public abstract class JAbsListFrgmt extends JBaseTitleFrgmt implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapperAdapter.OnMoreloadListener {
    @BindView(R.id.common_recv) public RecyclerView mCommonRecv;
    @BindView(R.id.common_swipe) public SwipeRefreshLayout mSwipeRefreshLayout;
    public List mListData = new ArrayList();
    public LoadMoreWrapperAdapter mRecvAdapter;

    @Override
    protected boolean requestNoTitleBar(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView = inflater.inflate(R.layout.jcommon_abslist, container);
        ButterKnife.bind(this, rootView);
        initRecView();
        initSwipeLayout();
    }

    private void initSwipeLayout(){
        mSwipeRefreshLayout.setEnabled(setEnableSwipeRefresh());
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecView(){
        MultiTypeAdapter multiTypeAdapter = new MultiTypeAdapter(mListData);
        mCommonRecv.setLayoutManager(setLayoutManager());
        register2Adapter(multiTypeAdapter);
        mRecvAdapter = new LoadMoreWrapperAdapter(multiTypeAdapter) {

            @Override
            public boolean enableUpMore(){
                return setEnableUpMore();
            }

            @Override
            public RecyclerHolder onCreateLoadingHolder(ViewGroup parent){
                return setLoadingHolder(parent);
            }

            @Override
            public void loadError(){
                setLoadError();
            }

            @Override
            public void noMoreLoad(){
                setNomoLoad();
            }
        };
        mRecvAdapter.setPagesize(setPageSize());
        mRecvAdapter.setOnMoreloadListener(this);
        mCommonRecv.setAdapter(mRecvAdapter);
    }

    public int setPageSize(){
        return 10;
    }

    public void setNomoLoad(){
        mRecvAdapter.noMoreLoad();
    }

    public void setLoadError(){
        mRecvAdapter.loadError();
    }

    protected RecyclerHolder setLoadingHolder(ViewGroup parent){
        return null;
    }

    protected boolean setEnableUpMore(){
        return true;
    }


    protected abstract RecyclerView.LayoutManager setLayoutManager();

    protected abstract void register2Adapter(MultiTypeAdapter multiTypeAdapter);

    @Override
    public void onRefresh(){
//        mSwipeRefreshLayout.setRefreshing(false);
    }


    public boolean setEnableSwipeRefresh(){
        return true;
    }

    @Override
    public void onLoadingMore(){

    }
}
