package com.blueprint.adapter.diff;

import com.blueprint.helper.interf.IRecvDataDiff;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/9/21 0021.
 */
public class JSimpleDiffCallback<D extends IRecvDataDiff> extends JBaseDiffCallback<D> {

    public JSimpleDiffCallback(List<D> oldList, List<D> newList){
        super(oldList,newList);
    }

    @Override
    protected boolean areItemsTheSame(D oldData, D newData){
        return oldData.areItemsTheSame(oldData,newData);
    }

    @Override
    protected boolean areContentsTheSame(D oldData, D newData){
        return oldData.areContentsTheSame(oldData,newData);
    }

    @Override
    protected Object getChangePayload(D oldData, D newData){
        return oldData.getChangePayload(oldData,newData);
    }

}
