package com.blueprint.basic.activity;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.adapter.frgmt.BaseFrgmtFractory;
import com.blueprint.adapter.frgmt.TabAdapter;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.helper.UIhelper;

import april.yun.JPagerSlidingTabStrip;
import april.yun.other.JTabStyleDelegate;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [具体内容由viewpager的fragment展示 此fragment只做容器装tabstrip + viewpager]</p>
 * <h>在onCreate里面会对tabStrip和viewpager进行一系列设置，所以子类需要在super之前获取相关数据</h>
 */
public abstract class JBaseTabVpStateActivity<SD> extends JBaseTitleStateActivity<SD> {

    public JPagerSlidingTabStrip mBaseTabStrip;
    public ViewPager mBaseViewpager;

    @Override
    protected JBasePresenter initPresenter(){
        //只是容器不需要 presenter
        return null;
    }

    /**
     * <h>在onCreate--onVreateContent里面会对tabStrip和viewpager进行一系列设置，所以子类需要在super.onCreate之前获取相关数据</h>
     *
     * @param inflater
     * @param fmContent
     */
    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout fmContent){
        View rootView = inflater.inflate(R.layout.jbasic_tab_vp_layout, fmContent);
        mBaseTabStrip = (JPagerSlidingTabStrip)rootView.findViewById(R.id.jbase_tab_strip);
        mBaseViewpager = (ViewPager)rootView.findViewById(R.id.jbase_viewpager);
        initTabStrip();
        setupAdapter();
    }

    protected void setupAdapter(){
        mBaseViewpager.setAdapter(new TabAdapter(getSupportFragmentManager(), setTabTitles(), setFrgmtProvider()));
        //        mBaseViewpager.setAdapter(new TabAdapter(getFragmentManager(), setTabTitles(), setFrgmtProvider()));
        mBaseViewpager.setOffscreenPageLimit(setTabTitles().length);
        //不需要加载数据 直接显示内容
        if(mBaseViewpager.getAdapter() instanceof TabAdapter) {
            //在onsizechange之后设置导致indicator不显示
            mBaseTabStrip.bindViewPager(mBaseViewpager);
        }else {
            mBaseTabStrip.setVisibility(View.GONE);
        }
    }

    protected void initTabStrip(){
        reConfigTabStrip(UIhelper.initTabStrip(mBaseTabStrip.getTabStyleDelegate()).setNeedTabTextColorScrollUpdate(true));
//        //        2，拿TabStyleDelegate
//        JTabStyleDelegate tabStyleDelegate = mBaseTabStrip.getTabStyleDelegate();
//        //        3, 用TabStyleDelegate设置属性
//        tabStyleDelegate.setShouldExpand(false)
//                //也可以直接传字符串的颜色，第一个颜色表示checked状态的颜色第二个表示normal状态
//                .setTextColor(findColor(R.color.colorPrimary), findColor(R.color.jforground_trans_gray))
//                .setTabTextSize(findDimens(R.dimen.tab_top_textsize)).setTabPadding(findDimens(R.dimen.tab_pading))
//                .setDividerPadding(0)//tab之间分割线 的上下pading
//                .setTabPadding(0).setUnderlineHeight(0)//底部横线的高度
//                .setIndicatorHeight(findDimens(R.dimen.tab_indicator_height))
//                .setUnderlineHeight(findDimens(R.dimen.tab_underline_height))
//                .setUnderlineColor(Color.parseColor("#e6e6e6")).setIndicatorColor(findColor(R.color.colorPrimary));
//        reConfigTabStrip(tabStyleDelegate);
    }

    protected void reConfigTabStrip(JTabStyleDelegate tabStyleDelegate){

    }

    protected abstract BaseFrgmtFractory setFrgmtProvider();

    protected abstract String[] setTabTitles();

}
