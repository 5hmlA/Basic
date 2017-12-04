package com.blueprint.adapter.hold;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.blueprint.Consistent;
import com.blueprint.adapter.RecyclerHolder;
import com.blueprint.helper.interf.OnViewClickListener;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static com.blueprint.LibApp.findString;

/**
 * @another 江祖赟
 * @date 2017/10/28 0028.
 */
public class DefaultLoadMoreBinder extends BaseLoadMoreBinder<BaseLoadMoreBinder.LoadMoreState> {

    public RecyclerHolder mLoadMoreHolder;
    private OnViewClickListener mViewClickListener;
    private CharSequence mNomoreLoadTipsIfneed = "== 我是有底线的 ==";

    public DefaultLoadMoreBinder(OnViewClickListener viewClickListener){
        mViewClickListener = viewClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull final LoadMoreState item){
        if(item.state.equals(FOOT_STATE_LOAD_ERROR)) {
            onLoadErrorState();
        }else if(item.state.equals(FOOT_STATE_LOAD_NOMORE)) {
            onLoadMoreState();
        }else {
            onNomoreLoadTips(item.tips);
        }
    }

    public void onLoadErrorClick(@NonNull Object item){
        if(FOOT_STATE_LOAD_ERROR.equals(mRootView.getTag(Consistent.ViewTag.view_tag5))) {
            mViewClickListener.onItemClicked(mRootView, item);
            //加载错误状态==点击===转为 加载状态！
            onLoadMoreState();
            //点击重试之后变成加载更多
        }else {
            //正常状态 ，一般不可达
        }
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
        mLoadMoreHolder = new RecyclerHolder(
                mRootView = inflater.inflate(com.blueprint.R.layout.default_recyc_loading_more, parent, false));
        rootViewLoadingTag(FOOT_STATE_LOAD_NOMORE);//holder处于 loadmore状态
        RxView.clicks(mLoadMoreHolder.itemView).throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception{
                        onLoadErrorClick(mLoadMoreHolder);
                    }
                });
        return mLoadMoreHolder;
    }

    /**
     * 设置holder的当前状态
     * 主要 用于 状态切换的时候 防止被多次设置同一个状态，没必要
     *
     * @param tag
     */
    public void rootViewLoadingTag(String tag){
        mRootView.setTag(Consistent.ViewTag.view_tag5, tag);
    }

    public boolean checkRootViewLoadingTag(String tag){
        return tag.equals(mRootView.getTag(Consistent.ViewTag.view_tag5));
    }

    /**
     * 重新设置holder到loadmore界面和状态
     */
    public void onLoadMoreState(){
        if(!checkRootViewLoadingTag(FOOT_STATE_LOAD_NOMORE)) {
            rootViewLoadingTag(FOOT_STATE_LOAD_NOMORE);
            mLoadMoreHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                    findString(com.blueprint.R.string.jonas_recyc_loading_more));
            mLoadMoreHolder.visibleViews(com.blueprint.R.id.recyc_item_pb_loadmore);
        }
    }

    /**
     * 重新设置holder到loaderror界面和状态
     */
    public void onLoadErrorState(){
        rootViewLoadingTag(FOOT_STATE_LOAD_ERROR);
        mLoadMoreHolder.visibleViews(com.blueprint.R.id.recyc_item_pb_loadmore);
        mLoadMoreHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore,
                findString(com.blueprint.R.string.jonas_recyc_load_retry));
    }

    public void onLoadCustomState(CharSequence msg){
        rootViewLoadingTag(FOOT_STATE_LOAD_ERROR);
        mLoadMoreHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore, msg);
        mLoadMoreHolder.goneViews(com.blueprint.R.id.recyc_item_pb_loadmore);
    }

    @Override
    public void onNomoreLoadTips(CharSequence msg){
        rootViewLoadingTag(FOOT_STATE_LOAD_FINISH);
        mLoadMoreHolder.goneViews(com.blueprint.R.id.recyc_item_pb_loadmore);
        if(!TextUtils.isEmpty(msg)) {
            mLoadMoreHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore, msg);
        }else {
            mLoadMoreHolder.setText(com.blueprint.R.id.recyc_item_tv_loadmore, mNomoreLoadTipsIfneed);
        }
    }

    @Override
    public void bindNomoreLoadTipsIfneed(CharSequence msg){
        mNomoreLoadTipsIfneed = msg;
    }
}
