package com.a4399.miniworld.vp.workshop.recommend;

import com.a4399.miniworld.vp.basic.BasePresenter;
import com.a4399.miniworld.vp.basic.BaseView;

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
