package com.baselib.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @des [recycleview适配器 基类,支持多种布局,拖拽,滑动删除]
 */
public abstract class BaseRecvAdapter<T> extends RecyclerView.Adapter<RecyclerHolder> implements DragSwipeAdapter {

    private final static String TAG = BaseRecvAdapter.class.getSimpleName();
    private SparseArray<Integer> mItemLayoutIds = new SparseArray<>();
    private List<T> mData = new ArrayList<>();

    /**
     * <p>需要重写{@link #getItemViewType(int)} </p>
     * @param data
     * @param itemLayoutId <p color="white">数组下标 作为item类型</p>
     */
    public BaseRecvAdapter(@NonNull List<T> data, int... itemLayoutId) {
        for (int i = 0; i < itemLayoutId.length; i++) {
            mItemLayoutIds.append(i, itemLayoutId[i]);
        }
        mData = data;
    }

    /**
     * <p>需要重写{@link #setItemLayouts(SparseArray)}和{@link #getItemViewType(int)} </p>
     * @param data
     */
    public BaseRecvAdapter(@NonNull List<T> data) {
        mData = data;
        setItemLayouts(mItemLayoutIds);
    }

    /**
     * 设置 type 和 布局 需要同时复写{@link #getItemViewType(int)}
     * <p color="white">itemLayoutIds.append(type,itemLayoutId);
     *
     * @param itemLayoutIds
     */
    protected void setItemLayouts(SparseArray<Integer> itemLayoutIds) {

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * @param viewType 和getItemViewType 相对应
     * @return
     */
    private int getItemTypeLayout(int viewType) {
        return mItemLayoutIds.get(viewType);
    }


    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(getItemTypeLayout(viewType), parent, false);
        return new RecyclerHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        convert(holder, position, mData.get(position));
    }

    public abstract void convert(RecyclerHolder holder, int position, T itemData);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void notifyDataChange(@NonNull List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void refreshDataChange(@NonNull List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void notifyDataChange(int position, @NonNull T data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position < mData.size()) {
            mData.remove(position);
            notifyItemRemoved(position);
        } else {
            Log.e(TAG, "position out of bounde of mData.size()");
        }
    }

    public void addItem(T data, int position) {
        if (position > mData.size()) {
            Log.e(TAG, position + " > mData.size():" + mData.size());
            return;
        }
        mData.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        removeItem(position);
    }

    /**
     * item的布局 嵌套一层 设置marging 用于添加阴影
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            View child = ((ViewGroup) viewHolder.itemView).getChildAt(0);
            viewHolder.itemView.setTag(child.getElevation());
            child.setElevation(6);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder) {
        View child = ((ViewGroup) viewHolder.itemView).getChildAt(0);
        child.setElevation(((Float) viewHolder.itemView.getTag()));
    }
}
