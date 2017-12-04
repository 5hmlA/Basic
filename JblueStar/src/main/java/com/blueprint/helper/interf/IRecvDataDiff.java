package com.blueprint.helper.interf;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
public interface IRecvDataDiff extends IRecvData {


    /**
     * 增删 判断的主要依据
     * <p>检查id之类</p>
     * @param oldData
     * @param newData
     * @return
     */
    public boolean areItemsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData);

    /**
     * areItemsTheSame为true才判断 areContentsTheSame
     * @param oldData
     * @param newData
     * @return
     */
    public boolean areContentsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData);

    public Object getChangePayload(IRecvDataDiff oldData, IRecvDataDiff newData);

}
