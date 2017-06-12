package com.blueprint.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.blueprint.LibApp.findString;
import static com.blueprint.helper.LogHelper.slog_d;
import static com.blueprint.helper.LogHelper.slog_e;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持]
 */
public class LoadMoreWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, DragSwipeAdapter {

    private static final int ITEMTYPE_LOADMORE = -13;
    private static final String TAG_LOADING = "loadingholder";
    private int PAGESIZE = 10;
    private final static String TAG = BaseRecvAdapter.class.getSimpleName();
    private MultiTypeAdapter mInnerAdapter;
    private List<Object> mData;
    private RecyclerHolder mLoadingHolder;
    private OnMoreloadListener mListener;
    private boolean mInLoadingMore;
    /**
     * <p> 1表示 可以加载更多
     * <p> 0 表示 没有更多可加载了
     */
    private int mLoadmoreitem = 1;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;


    /**
     * 多布局模式 支持上拉刷新
     */
    public LoadMoreWrapperAdapter(MultiTypeAdapter innerAdapter){
        mInnerAdapter = innerAdapter;
        mData = (List<Object>)mInnerAdapter.getItems();
        mLoadmoreitem = enableUpMore() ? 1 : 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        setSpanCount(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                if(!ViewCompat.canScrollVertically(recyclerView, 1) && mLoadmoreitem == 1 && !mInLoadingMore) {
                    mInLoadingMore = true;
                    if(mListener != null) {
                        mListener.onLoadingMore();
                    }
                }
            }
        });
    }

    private void setSpanCount(RecyclerView recv){
        final RecyclerView.LayoutManager layoutManager = recv.getLayoutManager();
        if(layoutManager != null) {
            if(layoutManager instanceof StaggeredGridLayoutManager) {
                mStaggeredGridLayoutManager = (StaggeredGridLayoutManager)layoutManager;
            }else if(layoutManager instanceof GridLayoutManager) {
                ( (GridLayoutManager)layoutManager ).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position){
                        return ITEMTYPE_LOADMORE == getItemViewType(position) ? ( (GridLayoutManager)layoutManager )
                                .getSpanCount() : 1;
                    }
                });
            }
        }else {
            Log.e(TAG, "LayoutManager 为空,请先设置 recycleView.setLayoutManager(...)");
        }
    }


    @Override
    public int getItemCount(){
        return mData.size()>PAGESIZE ? mData.size()+mLoadmoreitem : mData.size();
    }

    @Override
    public final int getItemViewType(int position){
        if(position == mData.size()) {
            return ITEMTYPE_LOADMORE;
        }else {
            return mInnerAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == ITEMTYPE_LOADMORE) {
            mLoadingHolder = onCreateLoadingHolder(parent);
            if(mLoadingHolder == null) {
                mLoadingHolder = new RecyclerHolder(
                        inflater.inflate(com.blueprint.R.layout.item_recyc_loading_more, parent, false));
                mLoadingHolder.setTag(TAG_LOADING);
                if(mStaggeredGridLayoutManager != null) {
                    StaggeredGridLayoutManager.LayoutParams fullSpanLayoutparam = new StaggeredGridLayoutManager.LayoutParams(
                            -1, -2);
                    fullSpanLayoutparam.setFullSpan(true);
                    ( (LinearLayout)mLoadingHolder.getView(com.blueprint.R.id.recyc_item_tv_loadmore).getParent() )
                            .setLayoutParams(fullSpanLayoutparam);
                }
                mLoadingHolder.setOnClickListener(com.blueprint.R.id.recyc_item_tv_loadmore, this);
            }
            return mLoadingHolder;
        }else {

            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position){
        if(position<mData.size()) {
            mInnerAdapter.onBindViewHolder(holder, position, mData);
        }
    }


    /**
     * 设置每页显示的数量
     *
     * @param pagesize
     * @return
     */
    public LoadMoreWrapperAdapter setPagesize(int pagesize){
        if(mData.size()<pagesize) {
            slog_d(TAG, "mData.size() < pagesize 不显示上拉加载状态");
        }
        this.PAGESIZE = pagesize;
        return this;
    }

    @Override
    public void onClick(View v){
        if(v.getId() == com.blueprint.R.id.recyc_item_tv_loadmore && !mInLoadingMore) {
            mInLoadingMore = true;
            //点击重试之后变成加载更多
            String s = findString(com.blueprint.R.string.jonas_recyc_loading_more);
            if(s.equals(
                    ( (TextView)mLoadingHolder.getView(com.blueprint.R.id.recyc_item_tv_loadmore) ).getText().toString()
                            .trim())) {
                slog_d(TAG, "点击加载更多");
                mLoadingHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                        findString(com.blueprint.R.string.jonas_recyc_loading_more));
                if(mListener != null && mLoadmoreitem == 1) {
                    mListener.onLoadingMore();
                }
            }
        }
    }

    /**
     * 外部手动调用 加载错误
     */
    public void loadError(){
        mLoadingHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                findString(com.blueprint.R.string.jonas_recyc_load_retry));
    }

    /**
     * 没有更多内容
     *
     * @return
     */
    public void noMoreLoad(){
        mLoadmoreitem = 0;
        notifyDataSetChanged();
    }

    public interface OnMoreloadListener {
        /**
         * 发起请求 加载更多数据/重试
         */
        void onLoadingMore();
    }

    public LoadMoreWrapperAdapter setOnMoreloadListener(OnMoreloadListener listener){
        mListener = listener;
        return this;
    }

    public void notifyDataChange(@NonNull List<Object> data){
        mInLoadingMore = false;
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if(position<mData.size()) {
            mData.remove(position);
            if(mData.size() == PAGESIZE && mLoadmoreitem == 1) {
                notifyDataSetChanged();
            }else {
                notifyItemRemoved(position);
            }
        }else {
            slog_e(TAG, "position out of bounde of mData.size()");
        }
    }


    public void addItem(Object data, int position){
        mInLoadingMore = false;
        if(position>mData.size()) {
            slog_e(TAG, position+" > mData.size():"+mData.size());
            return;
        }
        mData.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition){
        if(fromPosition<mData.size() && toPosition<mData.size()) {
            Collections.swap(mData, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position){
        if(position<mData.size()) {
            removeItem(position);
        }
    }

    /**
     * item的布局 嵌套一层 设置marging 用于添加阴影
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
        if(mLoadingHolder == viewHolder) {
            Log.e(TAG, "上拉加载提示holder不可以拖动滑动 ");
            return;
        }
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            View child = ( (ViewGroup)viewHolder.itemView ).getChildAt(0);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.itemView.setTag(child.getElevation());
                child.setElevation(6);
            }
        }
    }


    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder){
        if(mLoadingHolder == viewHolder) {
            slog_e(TAG, "上拉加载提示holder不可以拖动滑动 ");
            return;
        }
        if(viewHolder.itemView.getTag() != null) {
            View child = ( (ViewGroup)viewHolder.itemView ).getChildAt(0);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                child.setElevation(( (Float)viewHolder.itemView.getTag() ));
            }
        }
    }

    /**
     * 自定义实现上拉加载布局
     * 同时需要复写 {@link #loadError()},{@link #noMoreLoad()}
     *
     * @param parent
     * @return
     */
    public RecyclerHolder onCreateLoadingHolder(ViewGroup parent){
        return null;
    }

    protected boolean enableUpMore(){
        return true;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder){
        super.onViewAttachedToWindow(holder);
        ItemViewBinder binderByIndex = mInnerAdapter.getBinderByIndex(holder.getItemViewType());
        System.out.println(holder.getAdapterPosition()+"---onViewAttachedToWindow----"+binderByIndex);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView){
        super.onDetachedFromRecyclerView(recyclerView);

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder){
        super.onViewDetachedFromWindow(holder);

        ItemViewBinder binderByIndex = mInnerAdapter.getBinderByIndex(holder.getItemViewType());
        System.out.println(holder.getAdapterPosition()+"---onViewAttachedToWindow----"+binderByIndex);
    }
}
