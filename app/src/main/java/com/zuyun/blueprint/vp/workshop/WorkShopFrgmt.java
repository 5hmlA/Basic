package com.zuyun.blueprint.vp.workshop;

import android.support.v4.app.Fragment;

import com.zuyun.blueprint.vp.basic.BaseTabViewpagerFrgmt;
import com.zuyun.blueprint.vp.workshop.featured.FeaturedFrgmt;
import com.zuyun.blueprint.vp.workshop.material.MaterialFrgmt;
import com.zuyun.blueprint.vp.workshop.online.OnlineFrgmt;
import com.zuyun.blueprint.vp.workshop.player.PlayerFrgmt;
import com.zuyun.blueprint.vp.workshop.recommend.RecomFrgmt;
import com.zuyun.blueprint.vp.workshop.topic.TopicFrgmt;
import com.blueprint.adapter.frgmt.BaseFrgmtFractory;
import com.zuyun.blueprint.R;

import april.yun.other.JTabStyleDelegate;
import butterknife.BindArray;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [工坊]
 */
public class WorkShopFrgmt extends BaseTabViewpagerFrgmt {

    @BindArray(R.array.titles_sec_workshop) String[] mTabTitles;

    @Override
    protected BaseFrgmtFractory setFrgmtProvider(){
        return new WorkShopFrgmtProvider();
    }

    @Override
    protected String[] setTabTitles(){
        return mTabTitles;
    }

    @Override
    public String setTitle(){
        return null;
    }

    public static class WorkShopFrgmtProvider extends BaseFrgmtFractory {
        private static final int RECOMMEND = 0;
        private static final int FEATURED = 1;
        private static final int TOPIC = 2;
        private static final int ONLINE = 3;
        private static final int PLAYER = 4;
        private static final int MATERIAL = 5;

        public Fragment createFragment(int position) {
            Fragment fragment = fmCache.get(position);
            if (fragment == null) {
                switch (position) {
                    case RECOMMEND:
                        fragment = new RecomFrgmt();
                        break;
                    case FEATURED:
                        fragment = new FeaturedFrgmt();
                        break;
                    case TOPIC:
                        fragment = new TopicFrgmt();
                        break;
                    case ONLINE:
                        fragment = new OnlineFrgmt();
                        break;
                    case PLAYER:
                        fragment = new PlayerFrgmt();
                        break;
                    case MATERIAL:
                        fragment = new MaterialFrgmt();
                        break;
                }
                fmCache.put(position, fragment);
            }
            return fragment;
        }
    }

    @Override
    protected void reConfigTabStrip(JTabStyleDelegate tabStyleDelegate){
        super.reConfigTabStrip(tabStyleDelegate);
        tabStyleDelegate.setShouldExpand(true);
    }
}
