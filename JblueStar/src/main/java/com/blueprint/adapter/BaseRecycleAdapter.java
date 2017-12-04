package com.blueprint.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueprint.helper.interf.IRecvData;

import java.util.ArrayList;
import java.util.List;

/**
 * @des [recycleview适配器 基类,支持多种布局]
 */
public class BaseRecycleAdapter<T extends IRecvData> extends RecyclerView.Adapter<RecyclerHolder> {

    private final static String TAG = BaseRecycleAdapter.class.getSimpleName();
    private SparseArray<Integer> mItemLayoutIds = new SparseArray<>();
    private List<T> mData = new ArrayList<>();

    /**
     * <p>需要重写{@link #getItemViewType(int)} </p>
     * @param itemLayoutId <p color="white">数组下标 作为item类型</p>
     */
    public BaseRecycleAdapter(int... itemLayoutId) {
        for (int i = 0; i < itemLayoutId.length; i++) {
            mItemLayoutIds.append(i, itemLayoutId[i]);
        }
    }
    /**
     * <p>需要重写{@link #getItemViewType(int)} </p>
     * @param data
     * @param itemLayoutId <p color="white">数组下标 作为item类型</p>
     */
    public BaseRecycleAdapter(@NonNull List<T> data, int... itemLayoutId) {
        for (int i = 0; i < itemLayoutId.length; i++) {
            mItemLayoutIds.append(i, itemLayoutId[i]);
        }
        mData = data;
    }

     /**
     * <p>需要重写{@link #setItemLayouts(SparseArray)}和{@link #getItemViewType(int)} </p>
     * @param data
     */
    public BaseRecycleAdapter(@NonNull List<T> data) {
        mData = data;
        setItemLayouts(mItemLayoutIds);
    }

    /**
     * 设置 type 和 布局 需要同时复写getItemViewType
     * <p color="red">itemLayoutIds.append(type,itemLayoutId);
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
        View root = inflater.inflate(getItemTypeLayout(viewType), parent, false);//item布局最外层设置的高有效
//        View root = inflater.inflate(getItemTypeLayout(viewType), null); //item布局最外层设置的高无效
        return new RecyclerHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        mData.get(position).bindHolder(holder, null, null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

            }
        });
        convert(holder, position, mData.get(position));
    }

    public void convert(RecyclerHolder holder, int position, T itemData){}

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
}
