package com.blueprint.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blueprint.Consistent;
import com.blueprint.adapter.hold.BaseLoadMoreBinder;
import com.blueprint.adapter.hold.DefaultLoadMoreBinder;
import com.blueprint.adapter.hold.JRecvBaseBinder;
import com.blueprint.helper.LogHelper;
import com.blueprint.helper.interf.DragSwipeAdapter;
import com.blueprint.helper.interf.OnMoreloadListener;
import com.blueprint.helper.interf.OnViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static com.blueprint.Consistent.LoadMoreWrapper.NEED_UP2LOAD_MORE;
import static com.blueprint.Consistent.LoadMoreWrapper.NON_UP2LOAD_MORE;
import static com.blueprint.adapter.hold.BaseLoadMoreBinder.FOOT_STATE_LOAD_CUSTOM_TIP;
import static com.blueprint.adapter.hold.BaseLoadMoreBinder.FOOT_STATE_LOAD_FINISH;
import static com.blueprint.helper.CheckHelper.checkLists;
import static com.blueprint.helper.LogHelper.slog_d;
import static com.blueprint.helper.LogHelper.slog_e;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持]
 * 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public class LoadMoreWrapperAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DragSwipeAdapter, OnViewClickListener {

    /**
     * 底部loadingholder永远都在
     * <p>{@link #mLoadmoreitem} 将无效</p>
     */
    public static final int STYLE_FIX_LOADING_HOLDER = 120;
    /**
     * 设置{@link #enAbleLoadMore(boolean)}为false之后 将会隐藏底部loadingholder
     */
    public static final int STYLE_LOADING_HOLDER_GONE = 130;
    public int mLoadMoreWrapperStyle = STYLE_LOADING_HOLDER_GONE;

    public static final int ITEMTYPE_LOADMORE = -13;
    public static final String FOOT_STATE_LOAD_LOADING = "loadingholder_up2load_loading";
    public static final String FOOT_STATE_LOAD_ERROR = "up2load_error";
    public static final String FOOT_STATE_LOAD_NOMORE = "up2load_nomore";
    /**
     * 当状态为需要上拉加载 但是数量少于PAGESIZE的时候 关闭上拉加载
     * 情况一般不存在，告诉有下一页一定有，只有第一页数据不够显示完整一屏幕才会
     * 数据只有0条但是 外部设置需要上拉加载 还是需要显示上啦加载ITEM
     */
    public int PAGESIZE = 0;
    public final static String TAG = BaseRecvAdapter.class.getSimpleName();
    public RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;
    public List<Object> mData = new ArrayList<>();
    public BaseLoadMoreBinder mLoadingBinder;
    public OnMoreloadListener mListener;

    public StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    public RecyclerView mRecyclerView;
    public int mLastCheckDataSize;
    public RecyclerHolder mLoadMoreHolder;

    /**
     * <h1>状态</h1>
     * 是否正处于上拉加载 数据状态，{已调用 onUp2loadmore 还未拿到数据 true}
     */
    public boolean mInLoadingMore;

    /**
     * <h1>开关</h1>
     * 是否可以 上啦 抓取数据，和是否需要底部加载holder区分
     */
    public boolean mCanUp2LoadMore = true;
    /**
     * <h1>开关</h1>
     * <p> 1表示 可以加载更多
     * <p> 0 表示 没有更多可加载了
     * <p>当为0的时候 一定无法上啦抓数据<p/>
     */
    private int mLoadmoreitem = NON_UP2LOAD_MORE;

    private BaseLoadMoreBinder.LoadMoreState mLoadMoreState;


    /**
     * 多布局模式 支持上拉刷新
     */
    public LoadMoreWrapperAdapter(MultiTypeAdapter innerAdapter){
        mInnerAdapter = innerAdapter;
        mData = (List<Object>)innerAdapter.getItems();
        mLastCheckDataSize = mData.size();
    }

    /**
     * 多布局模式 支持上拉刷新
     */
    public LoadMoreWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> innerAdapter, List data){
        mInnerAdapter = innerAdapter;
        mData = data;
        mLastCheckDataSize = mData.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
        setSpanCount(recyclerView);
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount){
                super.onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload){
                super.onItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount){
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount){
                checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
            }

            @Override
            public void onChanged(){
                //数据数量 变化了才需要判断
                if(isLoadMoreEnable() && mData.size() != mLastCheckDataSize) {
                    //                if(mLoadmoreitem == NEED_UP2LOAD_MORE && mLastCheckDataSize == 0 || mData.size() != mLastCheckDataSize) {
                    LogHelper.Log_d("load_more 数据发生变化同时数据数量发生变化 检测是否需要触发上拉加载");
                    mLastCheckDataSize = mData.size();
                    checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                checkUp2loadMore(newState);
            }

            //            @Override
            //            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            //                super.onScrolled(recyclerView, dx, dy);
            //                if(mLoadmoreitem == NEED_UP2LOAD_MORE) {
            //                    //向上无法滚动
            //                    if(dy>0 && !mRecyclerView.canScrollVertically(1) && mLoadmoreitem == NEED_UP2LOAD_MORE && !mInLoadingMore) {
            //                        mInLoadingMore = true;
            //                        if(mListener != null) {
            //                            mListener.onup2LoadingMore();
            //                        }
            //                    }
            //                }
            //            }
        });
    }

    /**
     * <p>只在停止滚动的状态检测</p>
     * 检查 是否loadingholder可见，可见则回掉监听的onup2LoadingMore 去加载下一页数据
     *
     * @param newState
     */
    private void checkUp2loadMore(int newState){
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        int lastPosition = 0;
        //当前状态为停止滑动状态SCROLL_STATE_IDLE时
        if(isEnable2LoadMore() && getItemCount()>0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
            if(layoutManager instanceof GridLayoutManager) {
                //通过LayoutManager找到当前显示的最后的item的position
                lastPosition = ( (GridLayoutManager)layoutManager ).findLastVisibleItemPosition();
            }else if(layoutManager instanceof LinearLayoutManager) {
                lastPosition = ( (LinearLayoutManager)layoutManager ).findLastVisibleItemPosition();
            }else if(layoutManager instanceof StaggeredGridLayoutManager) {
                //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                int[] lastPositions = new int[( (StaggeredGridLayoutManager)layoutManager ).getSpanCount()];
                ( (StaggeredGridLayoutManager)layoutManager ).findLastVisibleItemPositions(lastPositions);
                lastPosition = findMax(lastPositions);
            }
            //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
            //如果相等则说明已经滑动到最后了
            if(lastPosition>=getItemCount()-1) {
                LogHelper.Log_d("loading 上拉提示 item 可见");
                if(!mInLoadingMore) {
                    loadLoading();
                    mInLoadingMore = true;
                    if(mListener != null) {
                        mListener.onup2LoadingMore();
                    }
                }
            }

            //                    if(mLoadingBinder != null && mLoadingBinder.itemView != null) {
            //                        //或者 loading可见自动加载 下一页
            //                        Rect visiRect = new Rect();
            //                        mLoadingBinder.itemView.getGlobalVisibleRect(visiRect);
            //                        System.out.println(visiRect.toString());
            //                        mLoadingBinder.itemView.getLocalVisibleRect(visiRect);
            //                        System.out.println(visiRect.toString());
            //                        mLoadingBinder.itemView.getWindowVisibleDisplayFrame(visiRect);
            //                        System.out.println(visiRect.toString());
            //                    }
        }
    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions){
        int max = lastPositions[0];
        for(int value : lastPositions) {
            if(value>max) {
                max = value;
            }
        }
        return max;
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
        return mData.size()+mLoadmoreitem;
    }

    @Override
    public final int getItemViewType(int position){
        if(position == mData.size()) {
            return ITEMTYPE_LOADMORE;
        }else if(position<mData.size()) {
            return mInnerAdapter.getItemViewType(position);
        }else {
            return ITEMTYPE_LOADMORE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == ITEMTYPE_LOADMORE) {
            mLoadingBinder = onCreateLoadmoreBinder(parent);
            if(mLoadingBinder == null) {
                mLoadingBinder = new DefaultLoadMoreBinder(this);
                mLoadMoreHolder = (RecyclerHolder)mLoadingBinder.onCreateViewHolder(inflater, parent);
                getLoadMoreStateBean();
            }
            if(mStaggeredGridLayoutManager != null) {
                StaggeredGridLayoutManager.LayoutParams fullSpanLayoutparam = new StaggeredGridLayoutManager.LayoutParams(
                        -1, -2);
                fullSpanLayoutparam.setFullSpan(true);
                ( (LinearLayout)mLoadMoreHolder.getView(com.blueprint.R.id.recyc_item_tv_loadmore).getParent() )
                        .setLayoutParams(fullSpanLayoutparam);
            }
            return mLoadMoreHolder;
        }else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    private BaseLoadMoreBinder.LoadMoreState getLoadMoreStateBean(){
        if(mLoadMoreState == null) {
            return createLoadmoreStateBean();
        }else {
            return mLoadMoreState;
        }
    }

    protected BaseLoadMoreBinder.LoadMoreState createLoadmoreStateBean(){
        return mLoadMoreState = new BaseLoadMoreBinder.LoadMoreState();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position){
        if(position<mData.size()) {
            mInnerAdapter.onBindViewHolder(holder, position, Collections.emptyList());
        }else if(isLoadMoreEnable()) {
            mLoadingBinder.onBindViewHolder(holder, mLoadMoreState);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, List<Object> payloads){
        if(position<mData.size()) {
            mInnerAdapter.onBindViewHolder(holder, position, payloads);
        }else if(isLoadMoreEnable()) {
            mLoadingBinder.onBindViewHolder(holder, mLoadMoreState, payloads);
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

    /**
     * 重回 加载状态
     */
    public void loadLoading(){
        if(mLoadingBinder != null) {
            mInLoadingMore = true;
            //mLoadingBinder不应该为null 默认不允许上啦加载会导致nullpointexception
            mLoadingBinder.onLoadMoreState();
        }else {
            LogHelper.Log_e("检查是否默认关闭了上拉加载");
        }
    }

    /**
     * 外部手动调用 加载错误
     */
    public void loadError(){
        if(mLoadingBinder != null) {
            mInLoadingMore = false;
            mLoadingBinder.onLoadErrorState();
            getLoadMoreStateBean().state = FOOT_STATE_LOAD_ERROR;
        }else {
            LogHelper.Log_e("检查是否默认关闭了上拉加载");
        }
    }

    /**
     * 外部手动调用 自定义 loading加载内容
     * 设置是否允许上啦加载数据 调用{@link #enAbleLoadMore}
     */
    public void loadCustomMsg(CharSequence tip, boolean canUp2LoadMore){
        mLoadmoreitem = NEED_UP2LOAD_MORE;
        if(TextUtils.isEmpty(tip)) {
            enAbleLoadMore(false);
        }else if(mLoadingBinder != null) {
            mCanUp2LoadMore = canUp2LoadMore;
            getLoadMoreStateBean().state = FOOT_STATE_LOAD_CUSTOM_TIP;
            getLoadMoreStateBean().tips = tip;
        }else {
            LogHelper.Log_e("检查是否默认关闭了上拉加载");
        }
    }

    /**
     * 外部手动调用 自定义 loading加载内容
     * 设置 不允许上啦加载数据，不涉及隐藏底部loadingholder
     */
    public void loadCustomMsg(CharSequence tip){
        loadCustomMsg(tip, false);
    }

    /**
     * 是否 需要 底部的 上拉加载holder
     *
     * @return
     */
    public boolean isLoadMoreEnable(){
        return mLoadmoreitem == NEED_UP2LOAD_MORE;
    }

    /**
     * 是否 允许上拉{同时需要允许显示底部的loadingholder} 抓取数据 回掉监听的 onup2LoadingMore
     *
     * @return
     */
    public boolean isEnable2LoadMore(){
        return mCanUp2LoadMore && isLoadMoreEnable();
    }

    /**
     * <p>在{@link #STYLE_FIX_LOADING_HOLDER}模式下 只控制是否允许上啦加载数据，
     * true 表示loadingholder处于loading状态，
     * false表示处于加载结束状态，显示结束提示信息{提示信息可以通过获取loadingholder设置{@link #getLoadingHolderBinder()}
     * 需要自定义提示内容可调用{@link #enAbleLoadMore(boolean, CharSequence)}
     * </p>
     * <p>在STYLE_LOADING_HOLDER_GONE模式下控制是否还有上拉的底部布局loadingholder同时控制是否允许上啦加载数据</p>
     * <h1>注意需要在notify之前调用，该方法会重新设置mCanUp2LoadMore</h1>
     *
     * @return
     */
    public void enAbleLoadMore(boolean enable){
        mInLoadingMore = false;
        if(mLoadMoreWrapperStyle == STYLE_FIX_LOADING_HOLDER) {
            mLoadmoreitem = NEED_UP2LOAD_MORE;
            mCanUp2LoadMore = enable;
            getLoadMoreStateBean().state = enable ? FOOT_STATE_LOAD_NOMORE : FOOT_STATE_LOAD_FINISH;
            getLoadMoreStateBean().tips = "";
        }else {
            if(enable) {
                mLoadmoreitem = NEED_UP2LOAD_MORE;
                mCanUp2LoadMore = true;
                getLoadMoreStateBean().state = FOOT_STATE_LOAD_NOMORE;
                getLoadMoreStateBean().tips = "";
            }else {
                mLoadmoreitem = NON_UP2LOAD_MORE;
                mCanUp2LoadMore = false;
            }
        }
    }

    /**
     * 同时将模式设置为 STYLE_FIX_LOADING_HOLDER
     *
     * @param enable
     * @param tips
     */
    public void enAbleLoadMore(boolean enable, CharSequence tips){
        mInLoadingMore = false;
        mLoadMoreWrapperStyle = STYLE_FIX_LOADING_HOLDER;
        mLoadmoreitem = NEED_UP2LOAD_MORE;
        mCanUp2LoadMore = enable;
        getLoadMoreStateBean().tips = tips;
        getLoadMoreStateBean().state = enable ? FOOT_STATE_LOAD_NOMORE : FOOT_STATE_LOAD_FINISH;
    }

    public LoadMoreWrapperAdapter setLoadeMoreWrapperStyle(int style){
        mLoadMoreWrapperStyle = style;
        return this;
    }

    public LoadMoreWrapperAdapter setOnMoreloadListener(OnMoreloadListener listener){
        mListener = listener;
        return this;
    }

    public void addMoreList(@NonNull List<T> data){
        if(checkLists(data)) {
            mInLoadingMore = false;
            int startposition = mData.size();
            mData.addAll(data);
            mLastCheckDataSize = mData.size();
            notifyItemRangeInserted(startposition, data.size());
        }
    }

    public void refreshAllData(@NonNull List<T> data){
        if(checkLists(data)) {
            mData.clear();
            mData.addAll(data);
            mLastCheckDataSize = data.size();
            mInLoadingMore = false;
            notifyDataSetChanged();
        }
    }

    public void changeAllData(@NonNull List<T> data){
        if(checkLists(data)) {
            mData.clear();
            mInLoadingMore = false;
            mData.addAll(data);
            mLastCheckDataSize = data.size();
        }
    }

    public void removeItem(int position){
        if(position<mData.size()) {
            mData.remove(position);
            if(isLoadMoreEnable() && mData.size() == PAGESIZE) {
                notifyDataSetChanged();
            }else {
                notifyItemRemoved(position);
            }
        }else {
            slog_e(TAG, "position out of bounde of mData.size()");
        }
    }

    public void removeItem(Object item){
        int index = mData.indexOf(item);
        if(index>-1) {
            mData.remove(index);
            if(isEnable2LoadMore() && mData.size() == PAGESIZE) {
                notifyDataSetChanged();
            }else {
                notifyItemRemoved(index);
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
        mLastCheckDataSize = mData.size();
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
        if(mLoadMoreHolder == viewHolder) {
            Log.e(TAG, "上拉加载提示holder不可以拖动滑动 ");
            return;
        }
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            View child = ( (ViewGroup)viewHolder.itemView ).getChildAt(0);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.itemView.setTag(Consistent.ViewTag.value_tag, child.getElevation());
                ViewCompat.setElevation(child, 6);
            }
        }
    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder){
        if(mLoadMoreHolder == viewHolder) {
            slog_e(TAG, "上拉加载提示holder不可以拖动滑动 ");
            return;
        }
        if(viewHolder.itemView.getTag(Consistent.ViewTag.value_tag) != null) {
            View child = ( (ViewGroup)viewHolder.itemView ).getChildAt(0);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                ViewCompat.setElevation(child, ( (Float)viewHolder.itemView.getTag(Consistent.ViewTag.value_tag) ));
            }
        }
    }

    /**
     * 自定义实现上拉加载布局
     * //最好包括 三种状态 上拉加载中,加载失败,没有更多 ,可自由切换
     * 同时需要复写 {@link #loadError()},{@link #enAbleLoadMore(boolean)} }
     *
     * @param parent
     * @return
     */
    public BaseLoadMoreBinder onCreateLoadmoreBinder(ViewGroup parent){
        return null;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder){
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView){
        super.onDetachedFromRecyclerView(recyclerView);
        if(mLoadingBinder != null) {
            mLoadingBinder.onDetachedFromRecyclerView(recyclerView);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder){
        super.onViewDetachedFromWindow(holder);
        if(mLoadingBinder != null) {
            mLoadingBinder.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onItemClicked(View view, Object itemData){
        if(!mInLoadingMore) {
            //没有正在拉取数据
            mInLoadingMore = true;
            slog_d(TAG, "点击加载更多");
            if(isLoadMoreEnable() && mListener != null) {
                mListener.retryUp2LoadingMore();
            }
        }
    }

    public RecyclerHolder getLoadingHolder(){
        return mLoadMoreHolder;
    }

    public JRecvBaseBinder getLoadingHolderBinder(){
        return mLoadingBinder;
    }

    //    public void onInserted(int position, int count) {
    //        mData.add(position, data);
    //        mInnerAdapter.notifyItemRangeInserted(position, count);
    //    }
    //
    //    public void onRemoved(int position, int count) {
    //        adapter.notifyItemRangeRemoved(position, count);
    //    }
    //
    //    public void onMoved(int fromPosition, int toPosition) {
    //        adapter.notifyItemMoved(fromPosition, toPosition);
    //    }
    //
    //    public void onChanged(int position, int count, Object payload) {
    //        adapter.notifyItemRangeChanged(position, count, payload);
    //    }
}
