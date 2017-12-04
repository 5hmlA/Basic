package com.blueprint.adapter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;

/**
 * @des [recycleview间隔对象]
 * @since [产品/模版版本]
 */
public class JDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mDivider;
    private Onscroll mListener;

    private float mScrolledPx;
    private float mScall;

    public JDividerItemDecoration(int divider){
        mDivider = divider;
//        mListener = new Onscroll();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
//        parent.addOnScrollListener(mListener);
        if(parent.getChildLayoutPosition(view) != 0) {
            outRect.top = mDivider;
        }
    }


    class Onscroll extends RecyclerView.OnScrollListener {
        private int mScrollState;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState){
            mScrollState = newState;
            if(newState == SCROLL_STATE_DRAGGING) {
//                System.out.println("lashen jianju");
            }else {
//                System.out.println("还原");
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if(mScrollState == SCROLL_STATE_DRAGGING) {
                mScrolledPx += dy;
//                mScall = Damping.calculateDamping(mScrolledPx);
//                System.out.println(mScrolledPx+"------------------------"+mScall);
            }else {
                mScrolledPx = 0;
            }
        }
    }
}
