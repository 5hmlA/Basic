package com.blueprint.adapter.hold;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/10/28 0028.
 */
public abstract class JRecvBaseBinder<T, VH extends RecyclerView.ViewHolder> {

    protected View mRootView;

    @NonNull
    public abstract VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void onBindViewHolder(@NonNull VH holder, @NonNull T item);

    public void onBindViewHolder(@NonNull VH holder, @NonNull T item, @NonNull List<Object> payloads){
        onBindViewHolder(holder, item);
    }

    public final int getPosition(@NonNull final RecyclerView.ViewHolder holder){
        return holder.getAdapterPosition();
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView){
    }

    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder){
    }
}
