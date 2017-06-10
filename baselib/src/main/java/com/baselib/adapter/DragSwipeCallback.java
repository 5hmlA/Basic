package com.baselib.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;


public class DragSwipeCallback extends ItemTouchHelper.Callback {

    private final static String TAG = DragSwipeCallback.class.getSimpleName();
    private DragSwipeAdapter mAdapter;

    public DragSwipeCallback(DragSwipeAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖动支持向下和向上
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;//拖动支持向下和向上
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;//滑动支持向左和向右
        return makeMovementFlags(dragFlags, swipeFlags);
        //return makeFlag(UP | DOWN|START | END, START | END);//这样无效
    }

    /**
     * true 支持长按拖动 长按每个holder都会调用这个判断
     * 或者手动调用 itemTouchHelper.startDrag(holder)
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * true 支持 滑动删除
     * 或者手动调用 itemTouchHelper.startSwipe(holder);
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.e(TAG, "onMove===== ");
        if (viewHolder instanceof RecyclerHolder) {
            if (RecyclerHolder.TAG_LOADING.equals(((RecyclerHolder) viewHolder).getTag())) {
                return false;
            }
        }
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof RecyclerHolder) {
            if (RecyclerHolder.TAG_LOADING.equals(((RecyclerHolder) viewHolder).getTag())) {
                return;
            }
        }
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 在每次View Holder的状态变成拖拽 (ACTION_STATE_DRAG 2) 或者 开始滑动 (ACTION_STATE_SWIPE 1)的时候被调用。
     * 松手后的滑动(ACTION_STATE_IDLE 0)
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        mAdapter.onSelectedChanged(viewHolder, actionState);
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 在一个view被拖拽或者sweep滑动 取消或者完成 结束的最终状态(还原，移除，换位)调用，
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        mAdapter.clearView(viewHolder);
        super.clearView(recyclerView, viewHolder);
    }
}