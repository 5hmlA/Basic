package com.blueprint.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.blueprint.adapter.RecyclerHolder;

import java.util.LinkedList;

/**
 * @another 江祖赟
 * @date 2017/8/25
 * { https://code.google.com/p/android/issues/detail?id=60464}
 */
public class ViewPagerFixed extends android.support.v4.view.ViewPager {
    public static final int TAG_VP_HOLDER = 0x11111111;
    private boolean mIsDisallowIntercept = false;

    public ViewPagerFixed(Context context){
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept){
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if(ev.getPointerCount()>1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        }else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        try {
            return super.onTouchEvent(ev);
        }catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        try {
            return super.onInterceptTouchEvent(ev);
        }catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 仿照 recyclerview.adapter 实现的具有 item view 复用功能的 PagerAdapter
     * thx: http://www.jianshu.com/p/3297f2a9f590
     */
    public abstract class ReusePagerAdapter<VH extends RecyclerHolder> extends PagerAdapter {

        private SparseArray<LinkedList<VH>> holders = new SparseArray<>(1);

        /**
         * 获取 item count
         *
         * @return count
         */
        public abstract int getItemCount();

        /**
         * 获取 view type
         *
         * @param position position
         * @return type
         */
        public int getItemViewType(int position) {
            return 0;
        }

        /**
         * 创建 holder
         *
         * @param parent   parent
         * @param viewType type
         * @return holder
         */
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * 绑定 holder
         *
         * @param holder   holder
         * @param position position
         */
        public abstract void onBindViewHolder(VH holder, int position);

        @Override
        public int getCount() {
            return getItemCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 获取 position 对应的 type
            int itemViewType = getItemViewType(position);
            // 根据 type 找到缓存的 list
            LinkedList<VH> holderList = holders.get(itemViewType);
            VH holder;
            if (holderList == null) {
                // 如果 list 为空，表示没有缓存
                // 调用 onCreateViewHolder 创建一个 holder
                holder = onCreateViewHolder(container, itemViewType);
                holder.itemView.setTag(TAG_VP_HOLDER, holder);
            } else {
                holder = holderList.pollLast();
                if (holder == null) {
                    // 如果 list size = 0，表示没有缓存
                    // 调用 onCreateViewHolder 创建一个 holder
                    holder = onCreateViewHolder(container, itemViewType);
                    holder.itemView.setTag(TAG_VP_HOLDER, holder);
                }
            }
            holder.position = position;
            holder.viewType = itemViewType;
            // 调用 onBindViewHolder 对 itemView 填充数据
            onBindViewHolder(holder, position);
            container.addView(holder.itemView);
            return holder.itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            VH holder = (VH) view.getTag(TAG_VP_HOLDER);
            int itemViewType = holder.viewType;
            LinkedList<VH> holderList = holders.get(itemViewType);
            if (holderList == null) {
                holderList = new LinkedList<>();
                holders.append(itemViewType, holderList);
            }
            // 缓存 holder
            holderList.push(holder);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
