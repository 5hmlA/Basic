package com.zuyun.blueprint.vp.raiders;

import android.support.v4.app.Fragment;

import com.zuyun.blueprint.vp.basic.BaseTabViewpagerFrgmt;
import com.zuyun.blueprint.vp.raiders.experience.ExperienceFrgmt;
import com.zuyun.blueprint.vp.raiders.news.NewsFrgmt;
import com.zuyun.blueprint.vp.raiders.question.QuestionFrgmt;
import com.zuyun.blueprint.vp.raiders.recommend.RecomFrgmt;
import com.zuyun.blueprint.vp.raiders.synthesis.SynthesisFrgmt;
import com.zuyun.blueprint.vp.raiders.turorial.TutorialFrgmt;
import com.blueprint.adapter.frgmt.BaseFrgmtFractory;
import com.zuyun.blueprint.R;

import butterknife.BindArray;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [攻略]
 */
public class RaidersFrgmt extends BaseTabViewpagerFrgmt {

    @BindArray(R.array.titles_sec_raiders) String[] mTabTitles;

    @Override
    public String setTitle(){
        return "攻略";
    }

    @Override
    protected BaseFrgmtFractory setFrgmtProvider(){
        return new RaidersFrgmtProvider();
    }

    @Override
    protected String[] setTabTitles(){
        return mTabTitles;
    }

//    @Override
//    protected void reConfigTabStrip(JTabStyleDelegate tabStyleDelegate){
//        super.reConfigTabStrip(tabStyleDelegate);
//        tabStyleDelegate.setShouldExpand(false)
//                //也可以直接传字符串的颜色，第一个颜色表示checked状态的颜色第二个表示normal状态
//                .setTextColor(Color.RED, Color.GRAY)
//                .setTabTextSize(findDimens(R.dimen.tab_top_textsize)).setTabPadding(findDimens(R.dimen.tab_pading))
//                .setDividerPadding(0)//tab之间分割线 的上下pading
//                .setTabPadding(0).setUnderlineHeight(0)//底部横线的高度
//                .setIndicatorHeight(18).setIndicatorColor(Color.parseColor("#45C01A"));
//    }

    private static class RaidersFrgmtProvider extends BaseFrgmtFractory {
        private static final int RECOMMEND = 0;
        private static final int EXPERIENCE = 1;
        private static final int TUTORIAL = 2;
        private static final int NEWS = 3;
        private static final int SYNTHESIS = 4;
        private static final int QUESTION = 5;

        public Fragment createFragment(int position){
            Fragment fragment = fmCache.get(position);
            if(fragment == null) {
                switch(position) {
                    case RECOMMEND:
                        fragment = new RecomFrgmt();
                        break;
                    case EXPERIENCE:
                        fragment = new ExperienceFrgmt();
                        break;
                    case TUTORIAL:
                        fragment = new TutorialFrgmt();
                        break;
                    case NEWS:
                        fragment = new NewsFrgmt();
                        break;
                    case SYNTHESIS:
                        fragment = new SynthesisFrgmt();
                        break;
                    case QUESTION:
                        fragment = new QuestionFrgmt();
                        break;
                }
                fmCache.put(position, fragment);
            }
            return fragment;
        }
    }

}
