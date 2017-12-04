package com.blueprint.helper.interf;

import android.support.annotation.Nullable;

import com.blueprint.adapter.RecyclerHolder;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
public interface IRecvData {
    /**
     *
     * @param holder
     * @param viewClickListener
     * @param payloads
     */
    void bindHolder(RecyclerHolder holder, OnViewClickListener viewClickListener, @Nullable List<Object> payloads);
}
