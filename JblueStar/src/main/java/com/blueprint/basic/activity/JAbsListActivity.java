package com.blueprint.basic.activity;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.Consistent;
import com.blueprint.LibApp;
import com.blueprint.R;
import com.blueprint.adapter.LoadMoreWrapperAdapter;
import com.blueprint.adapter.LoadMoreWrapperDampAdapter;
import com.blueprint.adapter.decoration.JDividerItemDecoration;
import com.blueprint.adapter.decoration.JLinearLayoutManager;
import com.blueprint.adapter.hold.DefaultLoadMoreBinder;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.basic.common.GeneralListContract;
import com.blueprint.basic.common.GeneralListPresenter;
import com.blueprint.helper.CheckHelper;
import com.blueprint.helper.interf.OnMoreloadListener;
import com.blueprint.rx.RxUtill;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.blueprint.Consistent.PageState.STATE_DATA_SUCCESS;
import static com.blueprint.Consistent.PageState.STATE_DOWN_REFRESH;
import static com.blueprint.Consistent.PageState.STATE_FIRST_LOAD;
import static com.blueprint.Consistent.PageState.STATE_UP2LOAD_MORE;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 * <android.support.v7.widget.RecyclerView
 * android:id="@+id/common_recv"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:clipToPadding="false"
 * android:paddingLeft="14dp"
 * android:paddingRight="14dp"
 * android:paddingTop="8dp">
 */
public abstract class JAbsListActivity<IT, SD> extends JBaseTitleStateActivity<List<SD>> implements GeneralListContract.View<IT,SD>, SwipeRefreshLayout.OnRefreshListener, OnMoreloadListener {
    public RecyclerView mCommonRecv;
    public List<IT> mListData = new ArrayList<IT>();
    public LoadMoreWrapperAdapter mRecvAdapter;
    public GeneralListPresenter<IT> mGeneralListPresenter;
    protected static final int NO_CUSTOM_LAYOUT = -10;
    protected int mCurrentPageState = STATE_FIRST_LOAD;

    @Override
    protected JBasePresenter initPresenter(){
        return mGeneralListPresenter = initListPresenter();
    }

    protected abstract GeneralListPresenter<IT> initListPresenter();

    //activity中默认显示titlebar
    @Override
    protected boolean requestNoTitleBar(){
        return false;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView = container;
        if(setCustomLayout() == NO_CUSTOM_LAYOUT) {
            if(forceNoSwipeRefresh() || mSwipeRefreshLayout != null) {
                //外层父布局 已经支持下拉刷新
                rootView = inflater.inflate(R.layout.jbasic_abslist_layout, container);
                mSwipeRefreshLayout = rootView.findViewById(R.id.jbase_swipe);
                initSwipeLayout();
            }else {
                //不允许同时开启两个swiperefreshlayout
                rootView = inflater.inflate(R.layout.jbasic_abslist_swipe_layout, container);
            }
        }else {
            rootView = inflater.inflate(setCustomLayout(), container);
        }
        mCommonRecv = rootView.findViewById(R.id.jbase_recv);
        initRecView();
    }

    /**
     * 支持多状态必然需要联网，默认支持下拉刷新
     * <h1>默认开启 最外层下拉刷新</h1>
     * <h1>复写{@link JBaseTitleStateActivity#forceNoSwipeRefresh()} 可以关闭所有下拉刷新 </h1>
     *
     * @return <p>true 开放 包裹多状态布局的下拉刷新</p>
     * <p>false 开放 包裹recycleview的下拉刷新</p>
     */
    public boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    @Override
    protected void toSubscribeData(Object from){
        mCurrentPageState = STATE_FIRST_LOAD;
        super.toSubscribeData(from);
    }

    /**
     * 必须包含 ID为 jbase_recv的 recycleview
     * 使用自定义recycleview布局 需要注意 是否需要关闭外层下拉刷新
     * <p>不需要的时候 重新 从布局寻找新的recycleview</p>
     *
     * @return
     */
    public int setCustomLayout(){
        return NO_CUSTOM_LAYOUT;
    }

    protected void initRecView(){
        MultiTypeAdapter multiTypeAdapter = new MultiTypeAdapter(mListData);
        mRecvAdapter = new LoadMoreWrapperDampAdapter(multiTypeAdapter) {
            @Override
            public DefaultLoadMoreBinder onCreateLoadmoreBinder(ViewGroup parent){
                return customLoadmoreBinder(parent);
            }
        };
        if(!LibApp.JELLYLIST) {
            ( (LoadMoreWrapperDampAdapter)mRecvAdapter ).mIsNeedDamp = false;
        }
        mCommonRecv.setLayoutManager(setLayoutManager());
        RecyclerView.ItemDecoration itemDecoration = setItemDecoration();
        if(itemDecoration != null) {
            mCommonRecv.addItemDecoration(itemDecoration);
        }
        mCommonRecv.setItemAnimator(new DefaultItemAnimator());
        register2Adapter(multiTypeAdapter);
        //        mRecvAdapter.setPagesize(setPageSize());
        mRecvAdapter.setOnMoreloadListener(this);
        mCommonRecv.setAdapter(mRecvAdapter);
    }

    public RecyclerView.ItemDecoration setItemDecoration(){
        return new JDividerItemDecoration(0);
    }

    @Override
    public void enAbleLoadMore(boolean enable, CharSequence tip){
        mRecvAdapter.enAbleLoadMore(enable);
    }

    /**
     * 通过该方法 创建的footview可以控制上拉加载,加载失败,没有更多 三种状态
     *
     * @param parent
     * @return
     */
    protected DefaultLoadMoreBinder customLoadmoreBinder(ViewGroup parent){
        return null;
    }

    protected RecyclerView.LayoutManager setLayoutManager(){
        return new JLinearLayoutManager(getApplicationContext());
    }

    protected abstract void register2Adapter(MultiTypeAdapter multiTypeAdapter);

    @Override
    public void onRefresh(){
        if(mGeneralListPresenter != null) {
            mCurrentPageState = STATE_DOWN_REFRESH;
            mGeneralListPresenter.down2RefreshData(mListData);
        }
    }

    @Override
    public void onup2LoadingMore(){
        if(mGeneralListPresenter != null) {
            mCurrentPageState = STATE_UP2LOAD_MORE;
            mGeneralListPresenter.up2LoadMoreData(mListData);
        }
    }

    @Override
    public void retryUp2LoadingMore(){
        if(mGeneralListPresenter != null) {
            mCurrentPageState = STATE_UP2LOAD_MORE;
            mGeneralListPresenter.retryUp2LoadMoreData(null);
        }
    }

    @Override
    public void showSucceed(List<SD> data){
        mRecvAdapter.refreshAllData(data);
        super.showSucceed(null);
    }

    @Override
    public void onMoreLoad(List<IT> moreData){
        mCurrentPageState = STATE_DATA_SUCCESS;
        if(CheckHelper.checkLists(moreData)) {
            mRecvAdapter.addMoreList(moreData);
        }else {
            //分页数据为空 删除后分页可能出现为空
            if(CheckHelper.checkLists(mListData)) {
                enAbleLoadMore(false, null);
                mRecvAdapter.notifyItemRemoved(mListData.size());//移除loading项
                //                mRecvAdapter.notifyDataSetChanged();
            }else {
                //第一页数据为空 加载下一页也为空 则显示空页面，场景，删除完第一页数据自动加载下一页的时候
                showError(Consistent.ErrorCode.ERROR_EMPTY);
            }
        }
    }

    public void showSucceed(final List<SD> data, final DiffUtil.Callback callback){
        if(!mListData.isEmpty()) {
            collectDisposables(Observable.create(new ObservableOnSubscribe<DiffUtil.DiffResult>() {
                @Override
                public void subscribe(ObservableEmitter<DiffUtil.DiffResult> e) throws Exception{
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback, true);
                    e.onNext(diffResult);
                    e.onComplete();
                }
            }).compose(RxUtill.<DiffUtil.DiffResult>defaultSchedulers_obser())
                    .subscribe(new Consumer<DiffUtil.DiffResult>() {
                        @Override
                        public void accept(DiffUtil.DiffResult diffResult) throws Exception{
                            hideLoading();
                            diffResult.dispatchUpdatesTo(mRecvAdapter);
                            mRecvAdapter.changeAllData(data);
                            dispatchUpdates();
                        }
                    }));
        }else {
            //华为某低端手机第一次会滚到底部 所以第一次不用diff
            mRecvAdapter.refreshAllData(data);
            super.showSucceed(null);
        }
    }

    public void dispatchUpdates(){

    }

    @Override
    public void showLoading(){
        mMultiStateLayout.showStateLoading();
    }

    /**
     * 复写注意 下拉刷新的隐藏和enable，参考父类
     *
     * @param eCode
     */
    @Override
    public void showError(int eCode){
        if(mCurrentPageState == STATE_UP2LOAD_MORE) {
            mRecvAdapter.loadError();
        }else {
            super.showError(eCode);
        }
    }

    @Override
    public void showError(Consistent.ErrorData error){
        showError(error.errorCode);
    }

//    public void hideLoading(){
//        mCurrentPageState = STATE_DATA_SUCCESS;
//        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isEnabled()) {
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//        mMultiStateLayout.showStateSucceed();
//        super.hideLoading();
//    }

    public final boolean isInStateUp2loadMore(){
        return mCurrentPageState == STATE_UP2LOAD_MORE;
    }
}
