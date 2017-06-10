package com.blueprint.adapter;

import android.support.v7.widget.RecyclerView;

public interface DragSwipeAdapter {
    /**
     * 拖拽
     * @param fromPosition
     * @param toPosition
     */
    void onItemMove(int fromPosition, int toPosition);

    /**
     * 滑动删除
     * @param position
     */
    void onItemDismiss(int position);

    /**
     * 在每次View Holder的状态变成拖拽 (ACTION_STATE_DRAG 2) 或者 开始滑动 (ACTION_STATE_SWIPE 1)的时候被调用。
     * 松手后的滑动(ACTION_STATE_IDLE 0)
     * @param viewHolder
     * @param actionState
     */
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);

    /**
     *  在一个view被拖拽或者sweep滑动 取消或者完成 调用，
     * @param viewHolder
     */
    void clearView(RecyclerView.ViewHolder viewHolder);
}
