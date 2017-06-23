package com.blueprint.basic;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.adapter.decoration.DividerItemDecoration;
import com.blueprint.adapter.LoadMoreWrapperAdapter;
import com.blueprint.adapter.RecyclerHolder;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 */
public abstract class JAbsListFrgmt extends JBaseTitleFrgmt implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapperAdapter.OnMoreloadListener {
    public RecyclerView mCommonRecv;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public List mListData = new ArrayList();
    public LoadMoreWrapperAdapter mRecvAdapter;

    @Override
    protected boolean requestNoTitleBar(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView;
        if(setCustomLayout() == -10) {
            rootView = inflater.inflate(R.layout.jcommon_abslist, container);
        }else {
            rootView = inflater.inflate(setCustomLayout(), container);
        }
        mCommonRecv = (RecyclerView)rootView.findViewById(R.id.common_recv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.common_swipe);
        initRecView();
        initSwipeLayout();
    }

    public int setCustomLayout(){
        return -10;
    }

    private void initSwipeLayout(){
        mSwipeRefreshLayout.setEnabled(setEnableSwipeRefresh());
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecView(){
        MultiTypeAdapter multiTypeAdapter = new MultiTypeAdapter(mListData);
        mCommonRecv.setLayoutManager(setLayoutManager());
        mCommonRecv.addItemDecoration(setItemDecoration());
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

    private RecyclerView.ItemDecoration setItemDecoration(){
        return new DividerItemDecoration(1);
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
        return false;
    }


    protected abstract RecyclerView.LayoutManager setLayoutManager();

    protected abstract void register2Adapter(MultiTypeAdapter multiTypeAdapter);

    @Override
    public void onRefresh(){
        //        mSwipeRefreshLayout.setRefreshing(false);
    }


    public boolean setEnableSwipeRefresh(){
        return false;
    }

    @Override
    public void onLoadingMore(){

    }
}
