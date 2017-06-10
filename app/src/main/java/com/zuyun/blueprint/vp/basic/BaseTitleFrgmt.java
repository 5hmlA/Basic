package com.zuyun.blueprint.vp.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.helper.StrHelper;
import com.blueprint.widget.JTitleBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.zuyun.blueprint.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import jonas.jlayout.MultiStateLayout;

import static com.blueprint.LibApp.setTextView;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 */
public abstract class BaseTitleFrgmt extends BaseFragment {
    public MultiStateLayout mMultiStateLayout;
    public JTitleBar mTitleBar;
    public BasePresenter mBasePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.common_titlefm_content, null);
        mMultiStateLayout = (MultiStateLayout)rootView.findViewById(R.id.common_frgmt_content);
        mTitleBar = (JTitleBar)rootView.findViewById(R.id.home_titlebar);
        if(requestNoTitleBar()) {
            mTitleBar.setVisibility(View.GONE);
        }
        initStableViews();
        onCreateContent(inflater, mMultiStateLayout);
        return rootView;
    }

    /**
     * 默认 有titlebar
     * @return
     */
    protected boolean requestNoTitleBar(){
        return false;
    }

    private void initStableViews(){
        //标题内容
        mTitleBar.setTitle(StrHelper.nullStrToEmpty(setTitle()));

        //空页面提示信息
        setTextView(mMultiStateLayout.getEmptyLayout(), R.id.j_multity_empt_msg, setEmptMsg());
        setTextView(mMultiStateLayout.getEmptyLayout(), R.id.j_multity_retry, setEmptRetryMsg());
        //错误页面提示信息
        setTextView(mMultiStateLayout.getErrorLayout(), R.id.j_multity_error_msg, setErrorMsg());
        setTextView(mMultiStateLayout.getErrorLayout(), R.id.j_multity_retry, setErrorRetryMsg());

        setClicks();
    }

    private void setClicks(){
        //左边的点击事件
        RxView.clicks(mTitleBar.getLiftIcon()).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
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

    @Override
    public void firstUserVisibile(){
        //todo 去掉测试的延时效果
        mMultiStateLayout.postDelayed(new Runnable() {
            @Override
            public void run(){
                if(mBasePresenter != null) {
                    mBasePresenter.subscribe();
                }
            }
        }, 1500);
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
     * @param inflater
     * @param container
     */
    protected abstract void onCreateContent(LayoutInflater inflater, RelativeLayout container);
}
