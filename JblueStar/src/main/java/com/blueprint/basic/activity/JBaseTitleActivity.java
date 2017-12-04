package com.blueprint.basic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.widget.JTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.blueprint.LibApp.setTextContent;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 * 在 onAttachtoWindow会获取数据
 * onContentChanged在setContentView内部调用 然后基类才获取各种控件，所以在onContentChanged中可以获取intent中的数据，就不需要在super.onCreate之前获取数据
 */
public abstract class JBaseTitleActivity extends JBaseActivity {
    public RelativeLayout mContentLayout;
    public JTitleBar mTitleBar;
    public JBasePresenter mBasePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBasePresenter = initPresenter();
        setContentView(R.layout.jbasic_title_layout);
        mContentLayout = findViewById(R.id.jbase_state_container);
        mTitleBar = findViewById(R.id.jbase_titlebar);
        if(requestNoTitleBar()) {
            mTitleBar.setVisibility(View.GONE);
        }else {
            initTitleBar();
            reConfigTitleBar(mTitleBar);
            if(setContentBelowTitleBar()) {
                stateLayoutBelowTitleBar();
            }
        }
        onCreateContent(getLayoutInflater(), mContentLayout);
        //        toSubscribeData();
    }

    public boolean setContentBelowTitleBar(){
        return true;
    }

    protected void stateLayoutBelowTitleBar(){
        RelativeLayout.LayoutParams stateLayoutParams = (RelativeLayout.LayoutParams)mContentLayout
                .getLayoutParams();
        stateLayoutParams.addRule(RelativeLayout.BELOW, R.id.jbase_titlebar);
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

    protected void initTitleBar(){
        //标题内容
        setTextContent(mTitleBar.getTitleTextView(), setTitle());
        setClicks();
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
        RxView.clicks(mTitleBar.getLeftIcon()).subscribe(new Consumer<Object>() {
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
        onBackPressed();
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

    /**
     * 类似 firstVisibility() 第一次获取数据入口
     */
    protected void toSubscribeData(){
        if(mBasePresenter != null) {
            mBasePresenter.subscribe(null);
        }
    }
}
