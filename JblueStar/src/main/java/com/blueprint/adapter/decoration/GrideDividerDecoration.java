package com.blueprint.adapter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @des [recycleview间隔对象]
 * @since [产品/模版版本]
 */
public class GrideDividerDecoration extends RecyclerView.ItemDecoration {

    private int mDivider;

    public GrideDividerDecoration(int divider){
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        GridLayoutManager gridLayoutManager = (GridLayoutManager)parent.getLayoutManager();
        int spanCount = gridLayoutManager.getSpanCount();
        //不是第一个的格子都设一个左边的间距
        outRect.left = mDivider;
        outRect.bottom = mDivider;
        //由于每行第一个，把左边距设为0
        if(parent.getChildLayoutPosition(view)%spanCount == 0) {
            outRect.left = 0;
        }
    }
}
