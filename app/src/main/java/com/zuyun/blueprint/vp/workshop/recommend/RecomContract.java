package com.zuyun.blueprint.vp.workshop.recommend;

import com.zuyun.blueprint.vp.basic.JBasePresenter;
import com.zuyun.blueprint.vp.basic.JBaseView;

import java.util.List;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class RecomContract {
    interface IRecomView extends JBaseView {
        void addLoopImageHolder(List<String> urls);
        void addUpdateHolder(boolean addHolder);
        void addHotHolder();
        void addUnstableHolder();
    }
    interface IRecoPresenter extends JBasePresenter {

        void loadLoopImage();

        void checkUpdate();

        void loadHot();

        void loadModule();
    }
}
