package com.blueprint.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.blueprint.helper.interf.IRecvData;
import com.blueprint.helper.interf.OnItemClickListener;
import com.blueprint.helper.interf.OnViewClickListener;

import java.util.List;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @another 江祖赟
 * @date 2017/6/30.
 */
public abstract class BaseBinder<D extends IRecvData> extends ItemViewBinder<D,RecyclerHolder> {

    OnItemClickListener<D> mItemClickListener;
    OnViewClickListener<D> mViewClickListener;

    public BaseBinder(){
    }

    public BaseBinder(OnItemClickListener<D> itemClickListener){
        mItemClickListener = itemClickListener;
    }

    public BaseBinder(OnViewClickListener<D> viewClickListener){
        mViewClickListener = viewClickListener;
    }

    public BaseBinder(OnItemClickListener<D> itemClickListener, OnViewClickListener<D> viewClickListener){
        mItemClickListener = itemClickListener;
        mViewClickListener = viewClickListener;
    }

//    @NonNull
//    @Override
//    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    @Override
    protected void onBindViewHolder(@NonNull final RecyclerHolder holder, @NonNull final D item){
        onBindViewHolder(holder,item,null);
    }

    @Override
    protected void onBindViewHolder(
            @NonNull final RecyclerHolder holder, @NonNull final D item, @NonNull List<Object> payloads){
        item.bindHolder(holder, mViewClickListener,payloads);
        if(mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    mItemClickListener.onItemClicked(item, holder.getAdapterPosition());
                }
            });
        }
    }
}
