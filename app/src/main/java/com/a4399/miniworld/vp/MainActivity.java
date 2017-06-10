package com.a4399.miniworld.vp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.a4399.miniworld.vp.basic.BaseActivity;
import com.a4399.miniworld.vp.dynamic.DynamicFrgmt;
import com.a4399.miniworld.vp.live.LiveFrgmt;
import com.a4399.miniworld.vp.me.MeFragmt;
import com.a4399.miniworld.vp.raiders.RaidersFrgmt;
import com.a4399.miniworld.vp.workshop.WorkShopFrgmt;
import com.baselib.adapter.frgmt.BaseFrgmtFractory;
import com.baselib.adapter.frgmt.TabAdapter;
import com.baselib.rx.RxBus;
import com.first.a4399.miniworld.R;

import april.yun.ISlidingTabStrip;
import april.yun.JPagerSlidingTabStrip;
import april.yun.other.JTabStyleDelegate;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.baselib.LibApp.findDimens;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_tab_buttom_strip) JPagerSlidingTabStrip mButtomTabStrip;
    @BindView(R.id.main_tab_pager) ViewPager mViewPager;
    @BindArray(R.array.titles_home_buttom) String[] mTitles;
    private int[] mNormal;
    private int[] mChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mNormal = new int[]{R.drawable.ic_tab_msg, R.drawable.ic_tab_contact, R.drawable.ic_tab_moments, R.drawable.ic_tab_profile, R.drawable.ic_tab_profile};
        mChecked = new int[]{R.drawable.ic_tab_msg_h, R.drawable.ic_tab_contact_h, R.drawable.ic_tab_moments_h, R.drawable.ic_tab_profile_h, R.drawable.ic_tab_profile_h}; //for getPageIconResId (需要selector) mSelectors = new int[] { R.drawable.tab_msg, R.drawable.tab_contact, R.drawable.tab_moment, R.drawable.tab_profile }; R.drawable.tab_msg

        initTagStrip();
        //activity里面用getSupportFragmentManager()  ,fragment中使用getChildFragmentManager()
        mViewPager.setAdapter(new ButtomPagerAdapter(getSupportFragmentManager(),mTitles,new HomeFrgmtProvider()));
        mButtomTabStrip.bindViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(mTitles.length);
    }

    private void initTagStrip(){
        //        2，拿TabStyleDelegate
        JTabStyleDelegate tabStyleDelegate = mButtomTabStrip.getTabStyleDelegate();
        //        3, 用TabStyleDelegate设置属性
        tabStyleDelegate.setTabIconGravity(Gravity.TOP).setShouldExpand(true)//用过的都知道干啥用的
//                .setFrameColor(Color.parseColor("#45C01A"))//边框颜色 设置为透明则不画边框
                //也可以直接传字符串的颜色，第一个颜色表示checked状态的颜色第二个表示normal状态
                .setTextColor(Color.parseColor("#45C01A"), Color.GRAY)
                .setTabTextSize(findDimens(R.dimen.tab_textsize))
                .setDividerPadding(0)//tab之间分割线 的上下pading
                .setTabPadding(0).setUnderlineHeight(0)//底部横线的高度
                .setCornerRadio(0);//设置滚动指示器和边框的圆角半径
    }


    public class ButtomPagerAdapter extends TabAdapter implements ISlidingTabStrip.IconTabProvider {

        public ButtomPagerAdapter(FragmentManager fm, String[] tabtiles, BaseFrgmtFractory frmtFractory){
            super(fm, tabtiles, frmtFractory);
        }

        //返回的是一个数组 第一个normal状态的icon 第二个checked状态下的
        @Override
        public int[] getPageIconResIds(int position){
            return new int[]{mChecked[position], mNormal[position]};

        }

        @Override
        public int getPageIconResId(int position){
            return 0;
        }

    }

    private static class HomeFrgmtProvider extends BaseFrgmtFractory {
        private static final int WORKSHOP = 0;
        private static final int DYNAMIC = 4;
        private static final int RAIDERS = 1;
        private static final int LIVE = 2;
        private static final int ME = 3;

        public Fragment createFragment(int position) {
            Fragment fragment = fmCache.get(position);
            if (fragment == null) {
                switch (position) {
                    case WORKSHOP:
                        fragment = new WorkShopFrgmt();
                        break;
                    case DYNAMIC:
                        fragment = new DynamicFrgmt();
                        break;
                    case RAIDERS:
                        fragment = new RaidersFrgmt();
                        break;
                    case LIVE:
                        fragment = new LiveFrgmt();
                        break;
                    case ME:
                        fragment = new MeFragmt();
                        break;
                }
                fmCache.put(position, fragment);
            }
            return fragment;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        RxBus.getInstance().dispose(0);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        RxBus.getInstance().unregisterAll();
    }
}
