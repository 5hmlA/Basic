package com.blueprint.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * @author yun.
 * @date 2017/6/12
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public interface ILifecycle {
    void onViewAttachedToWindow(RecyclerView.ViewHolder holder);
    void onViewDetachedFromWindow(RecyclerView.ViewHolder holder);
}
