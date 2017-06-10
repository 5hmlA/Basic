package com.a4399.miniworld.vp.workshop;

import android.support.v4.app.Fragment;

import com.a4399.miniworld.vp.basic.BaseTabViewpagerFrgmt;
import com.a4399.miniworld.vp.workshop.featured.FeaturedFrgmt;
import com.a4399.miniworld.vp.workshop.material.MaterialFrgmt;
import com.a4399.miniworld.vp.workshop.online.OnlineFrgmt;
import com.a4399.miniworld.vp.workshop.player.PlayerFrgmt;
import com.a4399.miniworld.vp.workshop.recommend.RecomFrgmt;
import com.a4399.miniworld.vp.workshop.topic.TopicFrgmt;
import com.baselib.adapter.frgmt.BaseFrgmtFractory;
import com.first.a4399.miniworld.R;

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
        return "工坊";
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

}
