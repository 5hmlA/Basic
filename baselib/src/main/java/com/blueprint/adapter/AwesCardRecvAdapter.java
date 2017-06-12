package com.blueprint.adapter;

import android.content.Context;
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

import static com.blueprint.helper.LogHelper.slog_d;
import static com.blueprint.helper.LogHelper.slog_e;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持]
 */
public abstract class AwesCardRecvAdapter<T> extends RecyclerView.Adapter<RecyclerHolder> implements View.OnClickListener, DragSwipeAdapter {

    private static final int ITEMTYPE_LOADMORE = -13;
    private static final String TAG_LOADING = "loadingholder";
    private int PAGESIZE = 10;
    private final static String TAG = BaseRecvAdapter.class.getSimpleName();
    private List<BaseAwesCard> mAwesCards;
    private Context mContext;
    private RecyclerHolder mLoadingHolder;
    private OnMoreloadListener mListener;
    /**
     * <p> 1表示 可以加载更多
     * <p> 0 表示 没有更多可加载了
     */
    private int mLoadmoreitem = 1;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    /**
     * 多布局模式 不支持上拉加载更多
     *
     * @param awesCards
     */
    public AwesCardRecvAdapter(@NonNull List<BaseAwesCard> awesCards){
        mLoadmoreitem = 0;
        mAwesCards = awesCards;
    }


    /**
     * 多布局模式 支持上拉刷新
     *
     * @param awesCards
     * @param recv
     */
    public AwesCardRecvAdapter(@NonNull List<BaseAwesCard> awesCards, @NonNull final RecyclerView recv){
        mAwesCards = awesCards;
        loadMoreInit(recv);
    }

    private void loadMoreInit(@NonNull final RecyclerView recv){
        mContext = recv.getContext();
        setSpanCount(recv);
        recv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                if(!ViewCompat.canScrollVertically(recv, 1) && mLoadmoreitem == 1) {
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
        return mAwesCards.size()>PAGESIZE ? mAwesCards.size()+mLoadmoreitem : mAwesCards.size();
    }

    /**
     * 每个 position 都对应一个类型的card
     *
     * @param position
     * @return
     */
    @Override
    public final int getItemViewType(int position){
        if(position == mAwesCards.size()) {
            return ITEMTYPE_LOADMORE;
        }else {
            return position;
        }
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null) {
            mContext = parent.getContext();
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == ITEMTYPE_LOADMORE) {
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
            return mLoadingHolder;
        }else {
            return mAwesCards.get(viewType).getRecyclerHolder(mContext);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, int position){
        if(position<mAwesCards.size()) {
            mAwesCards.get(position).holderConvert(holder, position);
        }
    }

    /**
     * 设置每页显示的数量
     *
     * @param pagesize
     * @return
     */
    public AwesCardRecvAdapter setPagesize(int pagesize){
        if(mAwesCards.size()<pagesize) {
            slog_d(TAG, "mAwesCards.size() < pagesize 不显示上拉加载状态");
        }
        this.PAGESIZE = pagesize;
        return this;
    }

    @Override
    public void onClick(View v){
        if(v.getId() == com.blueprint.R.id.recyc_item_tv_loadmore) {
            String s = mContext.getString(com.blueprint.R.string.jonas_recyc_loading_more);
            if(s.equals(
                    ( (TextView)mLoadingHolder.getView(com.blueprint.R.id.recyc_item_tv_loadmore) ).getText().toString()
                            .trim())) {
                slog_d(TAG, "点击加载更多");
                mLoadingHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                        mContext.getString(com.blueprint.R.string.jonas_recyc_loading_more));
                if(mListener != null && mLoadmoreitem == 1) {
                    mListener.onLoadingMore();
                }
            }
        }
    }

    /**
     * 外部手动调用 加载错误
     */
    public AwesCardRecvAdapter loadError(){
        mLoadingHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                mContext.getString(com.blueprint.R.string.jonas_recyc_load_retry));
        return this;
    }

    /**
     * 不需要 上拉刷新
     *
     * @return
     */
    public AwesCardRecvAdapter noMoreLoad(){
        mLoadmoreitem = 0;
        notifyDataSetChanged();
        return this;
    }

    public interface OnMoreloadListener {
        /**
         * 发起请求 加载更多数据
         */
        void onLoadingMore();
    }

    public AwesCardRecvAdapter setOnMoreloadListener(OnMoreloadListener listener){
        mListener = listener;
        return this;
    }

    public void notifyDataChange(@NonNull List<BaseAwesCard> data){
        mAwesCards.addAll(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if(position<mAwesCards.size()) {
            mAwesCards.remove(position);
            if(mAwesCards.size() == PAGESIZE && mLoadmoreitem == 1) {
                notifyDataSetChanged();
            }else {
                notifyItemRemoved(position);
            }
        }else {
            slog_e(TAG, "position out of bounde of mAwesCards.size()");
        }
    }


    public void addItem(BaseAwesCard data, int position){
        if(position>mAwesCards.size()) {
            slog_e(TAG, position+" > mAwesCards.size():"+mAwesCards.size());
            return;
        }
        mAwesCards.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition){
        if(fromPosition<mAwesCards.size() && toPosition<mAwesCards.size()) {
            Collections.swap(mAwesCards, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position){
        if(position<mAwesCards.size()) {
            removeItem(position);
        }
    }

    /**
     * item的布局 嵌套一层 设置marging 用于添加阴影
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
        if(mLoadingHolder == viewHolder) {
            slog_e(TAG, "上拉加载提示holder不可以拖动滑动 ");
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
}
