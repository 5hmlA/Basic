package com.blueprint.basic.frgmt;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.R;
import com.blueprint.adapter.frgmt.BaseFrgmtFractory;
import com.blueprint.adapter.frgmt.TabAdapter;
import com.blueprint.helper.UIhelper;

import april.yun.JPagerSlidingTabStrip;
import april.yun.other.JTabStyleDelegate;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [具体内容由viewpager的fragment展示 此fragment只做容器装tabstrip + viewpager]
 */
public abstract class JBaseTabVpFrgmt extends JBaseTitleFrgmt {


    public JPagerSlidingTabStrip mSecTabStrip;
    public ViewPager mSecViewpager;

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout fmContent){
        View rootView = inflater.inflate(R.layout.jbasic_tab_vp_layout, fmContent);
        mSecTabStrip = (JPagerSlidingTabStrip)rootView.findViewById(R.id.jbase_tab_strip);
        mSecViewpager = (ViewPager)rootView.findViewById(R.id.jbase_viewpager);
        initTabStrip();
        setupAdapter();
    }

    private void setupAdapter(){
        // http://blog.csdn.net/a1274624994/article/details/53575976
        //getFragmentManager()有问题 浪费好多时间：getChildFragmentManager  T_T
        mSecViewpager.setAdapter(new TabAdapter(getChildFragmentManager(), setTabTitles(), setFrgmtProvider()));
        mSecViewpager.setOffscreenPageLimit(setTabTitles().length);
        if(mSecViewpager.getAdapter() instanceof TabAdapter) {
            //在onsizechange之后设置导致indicator不显示
            mSecTabStrip.bindViewPager(mSecViewpager);
        }else {
            mSecTabStrip.setVisibility(View.GONE);
        }
    }

    private void initTabStrip(){
//        //        2，拿TabStyleDelegate
//        JTabStyleDelegate tabStyleDelegate = mBaseTabStrip.getTabStyleDelegate();
//        //        3, 用TabStyleDelegate设置属性
//        tabStyleDelegate.setShouldExpand(false)
//                //也可以直接传字符串的颜色，第一个颜色表示checked状态的颜色第二个表示normal状态
//                .setTextColor(Color.parseColor("#45C01A"), Color.GRAY)
//                .setTabTextSize(LibApp.findDimens(R.dimen.tab_top_textsize)).setTabPadding(LibApp.findDimens(R.dimen.tab_pading))
//                .setDividerPadding(0)//tab之间分割线 的上下pading
//                .setTabPadding(0).setUnderlineHeight(0)//底部横线的高度
//                .setIndicatorHeight(8).setIndicatorColor(Color.parseColor("#45C01A"));
        reConfigTabStrip(UIhelper.initTabStrip(mSecTabStrip.getTabStyleDelegate()).setNeedTabTextColorScrollUpdate(true));
    }

    protected void reConfigTabStrip(JTabStyleDelegate tabStyleDelegate){

    }

    protected abstract BaseFrgmtFractory setFrgmtProvider();

    protected abstract String[] setTabTitles();

}
