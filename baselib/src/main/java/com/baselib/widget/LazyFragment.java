package com.baselib.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @author 江祖赟.
 * @date 2017/6/9
 * @des [viewpager中current的fragment 先setUserVisibleHint后onCreateView，只在第一次可见的时候加载数据]
 */
public abstract class LazyFragment extends Fragment {

    private boolean mIsViewCreated;
    private boolean mIsVisibleToUser;
    private boolean mIsFirstVisibile = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mIsViewCreated = true;
        if(mIsVisibleToUser && mIsFirstVisibile) {
            mIsFirstVisibile = false;
            firstUserVisibile();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;

        if(isVisibleToUser && mIsViewCreated && mIsFirstVisibile) {
            mIsFirstVisibile = false;
            firstUserVisibile();
        }

    }

    /**
     * 其实没有必要 只要使用MultiStateLayout的状态即可
     * 初始状态STATE_UNMODIFY才需要加载数据其他不用自动加载数据
     */
    public abstract void firstUserVisibile();
}
