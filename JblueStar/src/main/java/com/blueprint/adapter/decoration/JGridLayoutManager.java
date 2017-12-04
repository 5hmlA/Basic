package com.blueprint.adapter.decoration;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @another 江祖赟
 * @date 2017/10/20 0020.
 */
public class JGridLayoutManager extends GridLayoutManager {


    public JGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public JGridLayoutManager(Context context, int spanCount){
        super(context, spanCount);
    }

    public JGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout){
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state){
        try {
            super.onLayoutChildren(recycler, state);
        }catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
