package com.blueprint.adapter.diff;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/9/21 0021.
 */
public abstract class JBaseDiffCallback<D> extends DiffUtil.Callback {
    protected List<D> oldList = new ArrayList<>();
    protected List<D> newList = new ArrayList<>();

    public JBaseDiffCallback(List<D> oldList, List<D> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize(){
        return oldList.size();
    }

    @Override
    public int getNewListSize(){
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition){
        return areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    /**
     * <p>判断是否类型相同，检查id之类</p>
     * @param oldData
     * @param newData
     * @return
     */
    protected abstract boolean areItemsTheSame(D oldData, D newData);

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition){
        return areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    protected abstract boolean areContentsTheSame(D oldData, D newData);

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition){
        D oldData = oldList.get(oldItemPosition);
        Object changePayload = getChangePayload(oldData, newList.get(newItemPosition));
        //旧数据放到新数据集合去 因为比较完差异之后 会将新数据刷到adapter ，adapter刷新的数据就是用新集合的数据，但是监听用的还是旧数据对象
        newList.remove(newItemPosition);
        newList.add(newItemPosition, oldData);
        return changePayload;
    }

    protected abstract Object getChangePayload(D oldData, D newData);

}
