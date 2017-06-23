package com.blueprint.basic.common;

import com.blueprint.basic.JBasePresenter;
import com.blueprint.basic.JBaseView;
import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/6/21.
 */
public class GeneralListContract {
    public interface View<T> extends JBaseView {
        void onMoreLoad(List<T> containerData);
    }

    public interface Presenter<T> extends JBasePresenter {

        void search(String key);//success显示数据

        void up2LoadMoreData(List<T> containerData);

        void down2RefreshData(List<T> containerData);//success显示数据

    }
}
