package com.blueprint.adapter;

import android.content.Context;

/**
 * @des [recycleview的holder 包括填充数据]
 */

public abstract class BaseAwesCard {

    /**
     * 创建holder
     * @return
     * @param context
     */
    public abstract RecyclerHolder getRecyclerHolder(Context context);

    /**
     * 填充数据
     * holder可见的时候被调用
     * @param holder
     * @param position
     */
    public abstract void holderConvert(RecyclerHolder holder, int position);
}
