package com.blueprint.basic.frgmt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.widget.JTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.blueprint.LibApp.setTextView;
import static com.blueprint.helper.LogHelper.Log_e;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 * onCreate中获取 getArguments数据
 */
public abstract class JBaseTitleFrgmt extends JBaseFragment {
    public RelativeLayout mContentLayout;
    public JTitleBar mTitleBar;
    public JBasePresenter mBasePresenter;

    protected JBasePresenter initPresenter(){
        return null;
    }

    @Override
    public void firstUserVisibile(){
        //不自动获取数据
        //toSubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        mBasePresenter = initPresenter();
        View rootView = inflater.inflate(R.layout.jbasic_title_layout, null);
        mContentLayout = rootView.findViewById(R.id.jbase_state_container);
        mTitleBar = rootView.findViewById(R.id.jbase_titlebar);
        if(requestNoTitleBar()) {
            mTitleBar.setVisibility(View.GONE);
        }else {
            initTitleBar();
            reconfigTitlebar(mTitleBar);
            if(setContentBelowTitleBar()) {
                stateLayoutBelowTitleBar();
            }
        }
        onCreateContent(inflater, mContentLayout);
        return rootView;
    }

    public boolean setContentBelowTitleBar(){
        return true;
    }

    protected void stateLayoutBelowTitleBar(){
        RelativeLayout.LayoutParams stateLayoutParams = (RelativeLayout.LayoutParams)mContentLayout.getLayoutParams();
        stateLayoutParams.addRule(RelativeLayout.BELOW, R.id.jbase_titlebar);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setClicks();
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
        setTextView(mTitleBar.getTitleTextView(), setTitle());
        int titleColor = setTitleColor();
        if(titleColor != 0) {
            mTitleBar.getTitleTextView().setTextColor(titleColor);
        }
        //        //空页面提示信息
        //        setTextView(mContentLayout.getEmptyLayout(), R.id.j_multity_empt_msg, setEmptMsg());
        //        setTextView(mContentLayout.getEmptyLayout(), R.id.j_multity_retry, setEmptRetryMsg());
        //        //错误页面提示信息 只有出现错误页面才加载错误页面 应该注册文字
        //        setTextView(mContentLayout.getErrorLayout(), R.id.j_multity_error_msg, setErrorMsg());
        //        setTextView(mContentLayout.getErrorLayout(), R.id.j_multity_retry, setErrorRetryMsg());


    }

    protected int setTitleColor(){
        return 0;
    }

    /**
     * 复写 更新titlebar样式
     *
     * @param titleBar
     */
    protected void reconfigTitlebar(JTitleBar titleBar){

    }

    protected void setClicks(){
        //左边的点击事件
        RxView.clicks(mTitleBar.getLeftIcon()).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object aVoid) throws Exception{
                doTitleBarLeftClick();
            }
        });
        RxView.clicks(mTitleBar.getRightIcon()).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object aVoid) throws Exception{
                doTitleBarRightClick();
            }
        });
    }

    protected void doTitleBarRightClick(){

    }

    protected void doTitleBarLeftClick(){
        if(getActivity() != null) {
            getActivity().onBackPressed();
        }
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

    protected String setTitle(){
        return null;
    }

    protected void toSubscribe(){
        if(mBasePresenter != null) {
            mBasePresenter.subscribe(null);
        }else {
            Log_e("mBasePresenter没有赋值---");
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
    public void onDestroy(){
        super.onDestroy();
        if(mBasePresenter != null) {
            mBasePresenter.unsubscribe();
        }
    }
}
