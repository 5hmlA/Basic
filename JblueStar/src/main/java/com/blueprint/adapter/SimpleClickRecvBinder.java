package com.blueprint.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.blueprint.helper.interf.IRecvData;
import com.blueprint.helper.interf.OnItemClickListener;
import com.blueprint.helper.interf.OnViewClickListener;

/**
 * @another 江祖赟
 * @date 2017/6/30.
 */
public class SimpleClickRecvBinder<D extends IRecvData> extends BaseBinder<D> {

    private int mLayoutIds;

    public SimpleClickRecvBinder(OnViewClickListener<D> viewClickListener, int layoutIds){
        super(viewClickListener);
        mLayoutIds = layoutIds;
    }

    public SimpleClickRecvBinder(OnItemClickListener<D> itemClickListener, OnViewClickListener<D> viewClickListener, int layoutIds){
        super(itemClickListener,viewClickListener);
        mLayoutIds = layoutIds;
    }

    public SimpleClickRecvBinder(int layoutIds){
        super();
        mLayoutIds = layoutIds;
    }

    @NonNull
    @Override
    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
        return new RecyclerHolder(inflater.inflate(mLayoutIds, parent, false));
    }
}
