package com.blueprint.basic.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.Consistent;
import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.basic.JBaseView;
import com.blueprint.widget.JTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import jonas.jlayout.MultiStateLayout;
import jonas.jlayout.OnStateClickListener;

import static com.blueprint.Consistent.ErrorCode.ERROR_EMPTY;
import static com.blueprint.Consistent.PageState.STATE_DATA_EMPTY;
import static com.blueprint.Consistent.PageState.STATE_DATA_ERROR;
import static com.blueprint.Consistent.PageState.STATE_DATA_SUCCESS;
import static com.blueprint.Consistent.PageState.STATE_FIRST_LOAD;
import static com.blueprint.LibApp.setTextContent;
import static com.blueprint.LibApp.setTextView;
import static com.blueprint.helper.LogHelper.Log_e;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 * 在 onAttachtoWindow会获取数据
 * onContentChanged在setContentView内部调用 然后基类才获取各种控件，所以在onContentChanged中可以获取intent中的数据，就不需要在super.onCreate之前获取数据
 */
public abstract class JBaseTitleStateActivity<SD> extends JBaseActivity implements JBaseView<SD>, OnStateClickListener, SwipeRefreshLayout.OnRefreshListener {
    public MultiStateLayout mMultiStateLayout;
    public JTitleBar mTitleBar;
    public JBasePresenter mBasePresenter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    protected int mCurrentPageState = STATE_FIRST_LOAD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBasePresenter = initPresenter();
        if(!forceNoSwipeRefresh() && setEnableOuterSwipeRefresh()) {
            //支持下拉刷新
            setContentView(R.layout.jbasic_title_state_swipe_layout);
            mTitleBar = findViewById(R.id.jbase_titlebar);
            mSwipeRefreshLayout = findViewById(R.id.jbase_swipe);
            mMultiStateLayout = findViewById(R.id.jbase_state_container);
            initSwipeLayout();
        }else {
            setContentView(R.layout.jbasic_title_state_layout);
            mTitleBar = findViewById(R.id.jbase_titlebar);
            mMultiStateLayout = findViewById(R.id.jbase_state_container);
        }
        mMultiStateLayout.setOnStateClickListener(this);
        if(requestNoTitleBar()) {
            mTitleBar.setVisibility(View.GONE);
        }else {
            initTitleBar();
            reConfigTitleBar(mTitleBar);
            if(setContentBelowTitleBar()) {
                contentLayoutBelowTitleBar();
            }
        }
        onCreateContent(getLayoutInflater(), mMultiStateLayout);
        try2findCustomSwipeRefreshLayout();
        //默认一定有下拉刷新，，最后开放方法  关闭下拉刷新
        if(forceNoSwipeRefresh()) {
            mSwipeRefreshLayout = null;
        }
        toSubscribeData(null);
    }

    public boolean setContentBelowTitleBar(){
        return true;
    }

    protected void contentLayoutBelowTitleBar(){
        View host = mMultiStateLayout;
        if(mSwipeRefreshLayout != null) {
            host = mSwipeRefreshLayout;//此下拉刷新一定是最外层的
        }
        RelativeLayout.LayoutParams stateLayoutParams = (RelativeLayout.LayoutParams)host.getLayoutParams();
        stateLayoutParams.addRule(RelativeLayout.BELOW, R.id.jbase_titlebar);
    }

    /**
     * 寻找自定义布局中的下拉刷新 id固定
     */
    private void try2findCustomSwipeRefreshLayout(){
        if(!forceNoSwipeRefresh() && mSwipeRefreshLayout == null) {
            //使用自己的布局的时候看看有没有swipelayout
            configCustomSwipeRefreshLayout(R.id.jbase_swipe);
        }
    }


    protected void configCustomSwipeRefreshLayout(@IdRes int srlId){
        mSwipeRefreshLayout = findViewById(srlId);
        initSwipeLayout();
    }

    /**
     * 默认开启下拉刷新 ，强制关闭下拉刷新，不要复写 onCreateView()
     *
     * @return
     */
    public boolean forceNoSwipeRefresh(){
        return false;
    }

    /**
     * 支持多状态必然需要联网，默认支持下拉刷新
     *
     * @return
     */
    protected boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    protected abstract JBasePresenter initPresenter();

    //onResume之后
    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();

    }

    @Override
    public void onContentChanged(){
        super.onContentChanged();
        //该方法是在 setContentView()内部触发的 并不是onCreate结束之后才执行
        //        @Override
        //        public void setContentView(View v) {
        //            ensureSubDecor();
        //            ViewGroup contentParent = (ViewGroup) mSubDecor.findViewById(android.R.id.content);
        //            contentParent.removeAllViews();
        //            contentParent.addView(v);
        //            mOriginalWindowCallback.onContentChanged();
        //        }
    }

    /**
     * 默认 有titlebar
     *
     * @return
     */
    protected boolean requestNoTitleBar(){
        return false;
    }

    private void initTitleBar(){
        //标题内容
        setTextContent(mTitleBar.getTitleTextView(), setTitle());
        int titleBarColor = setTitleBarColor();
        if(titleBarColor != Consistent.DEFAULTERROR) {
            mTitleBar.getTitleTextView().setTextColor(titleBarColor);
        }
        //空页面提示信息
        setTextView(mMultiStateLayout.getEmptyLayout(), R.id.j_multity_empt_msg, setEmptMsg());
        setTextView(mMultiStateLayout.getEmptyLayout(), R.id.j_multity_retry, setEmptRetryMsg());
        //错误页面提示信息
        setTextView(mMultiStateLayout.getErrorLayout(), R.id.j_multity_error_msg, setErrorMsg());
        setTextView(mMultiStateLayout.getErrorLayout(), R.id.j_multity_retry, setErrorRetryMsg());
        mTitleBar.post(new Runnable() {
            @Override
            public void run(){
                setClicks();
            }
        });
    }

    protected int setTitleBarColor(){
        return Consistent.DEFAULTERROR;
    }

    protected void initSwipeLayout(){
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    /**
     * 复写 更新titlebar样式
     *
     * @param titleBar
     */
    protected void reConfigTitleBar(JTitleBar titleBar){

    }

    protected void setClicks(){
        //左边的点击事件
        collectDisposables(RxView.clicks(mTitleBar.getLeftIcon()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object aVoid) throws Exception{
                doOnTBleftClick();
            }
        }));
        collectDisposables(RxView.clicks(mTitleBar.getRightIcon()).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object aVoid) throws Exception{
                        doOnTBrightClick();
                    }
                }));
    }

    protected void doOnTBrightClick(){

    }

    protected void doOnTBleftClick(){
        onBackPressed();
    }

    protected CharSequence setErrorRetryMsg(){
        return null;
    }

    protected CharSequence setEmptRetryMsg(){
        return null;
    }

    protected String setErrorMsg(){
        return "";
    }

    protected String setEmptMsg(){
        return "";
    }

    protected Object setTitle(){
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mBasePresenter != null) {
            mBasePresenter.unsubscribe();
        }
    }

    /**
     * 将布局添加到 container中
     *
     * @param inflater
     * @param container
     */
    protected abstract void onCreateContent(LayoutInflater inflater, RelativeLayout container);

    @Override
    public void onRetry(@MultiStateLayout.LayoutState int layoutState){
        mMultiStateLayout.showStateLoading();
        toSubscribeData(null);
    }

    /**
     * 默认 onretry传的参数为null 第一次加载参数也为null
     *
     * @param from
     */
    protected void toSubscribeData(@Nullable Object from){
        mCurrentPageState = STATE_FIRST_LOAD;
        if(mBasePresenter != null) {
            mBasePresenter.subscribe(from);
        }else {
            Log_e("mBasePresenter没有赋值---");
        }
    }

    @Override
    public void onLoadingCancel(){
        //todo
    }

    @Override
    public void onRefresh(){
        toSubscribeData(null);
    }

    @Override
    public void showLoading(){
        mMultiStateLayout.showStateLoading();
    }

    @Override
    public void showError(Consistent.ErrorData error){
        showError(error.errorCode);
    }

    @Override
    public void showSucceed(SD data){
        hideLoading();
    }

    public void hideLoading(){
        mCurrentPageState = STATE_DATA_SUCCESS;
        //下拉刷新成功，网络错误重试(下拉不可用--转可用)
        switchSwipeRefresh(true);
        mMultiStateLayout.showStateSucceed();
    }

    public void showError(int eCode){
        if(eCode == ERROR_EMPTY) {
            mCurrentPageState = STATE_DATA_EMPTY;
            //允许下拉刷新
            switchSwipeRefresh(true);
            mMultiStateLayout.showStateEmpty();
        }else {
            mCurrentPageState = STATE_DATA_ERROR;
            //网络错误数据错误 关闭下拉刷新
            switchSwipeRefresh(false);
            mMultiStateLayout.showStateError();
        }
    }

    protected void switchSwipeRefresh(boolean enable){
        if(mSwipeRefreshLayout != null) {
            //只有下拉 才出现下拉刷新  或者手动调用setRefresh
            mSwipeRefreshLayout.setRefreshing(false);//内部有判断和当前状态是否相同
            mSwipeRefreshLayout.setEnabled(enable);//内部有判断和当前状态是否相同
        }
    }

}
