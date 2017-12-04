package com.blueprint.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.blueprint.helper.Damping;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持]
 */
public class LoadMoreWrapperDampAdapter<T> extends LoadMoreWrapperAdapter<T> {

    public boolean mIsNeedDamp = true;

    /**
     * 多布局模式 支持上拉刷新
     *
     * @param innerAdapter
     */
    public LoadMoreWrapperDampAdapter(MultiTypeAdapter innerAdapter){
        super(innerAdapter);
    }

    public LoadMoreWrapperDampAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> innerAdapter, List data){
        super(innerAdapter, data);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        if(mIsNeedDamp) {
            Damping.wrapper(mRecyclerView).configDirection(LinearLayout.VERTICAL);
        }
    }
}
