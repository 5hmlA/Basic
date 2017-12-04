package com.blueprint.basic.common;

import com.blueprint.basic.JBaseView;
import com.blueprint.basic.JBasePresenter;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/6/21.
 */
public class GeneralListContract {
    //页面可能存在多种数据 list列表 只是其中一种/如果showSucceed的数据是复杂的Object那就返回一个元素的集合即可
    public interface View<IT, SD> extends JBaseView<List<SD>> {

        void onMoreLoad(List<IT> moreData);

        /**
         * 是否允许上拉加载
         *
         * @param enable
         * @param tip 底部holder在制定模式下显示的提示信息
         */
        void enAbleLoadMore(boolean enable, CharSequence tip);

    }

    public interface Presenter<IT> extends JBasePresenter<Object> {

        void search(String key);//success显示数据

        void up2LoadMoreData(List<IT> containerData);

        void down2RefreshData(List<IT> containerData);//success显示数据

        void down2RefreshData(Object fromParam);//success显示数据

        void retryUp2LoadMoreData(List<IT> containerData);//上拉加载 失败重试
    }
}
