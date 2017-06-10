package com.zuyun.blueprint.vp.workshop.recommend;

import com.zuyun.blueprint.vp.basic.BasePresenter;
import com.zuyun.blueprint.vp.basic.BaseView;

import java.util.List;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class RecomContract {
    interface IRecomView extends BaseView{
        void addLoopImageHolder(List<String> urls);
        void addUpdateHolder(boolean addHolder);
        void addHotHolder();
        void addUnstableHolder();
    }
    interface IRecoPresenter extends BasePresenter {

        void loadLoopImage();

        void checkUpdate();

        void loadHot();

        void loadModule();
    }
}
