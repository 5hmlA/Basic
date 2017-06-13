package com.blueprint.basic;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [由界面触发的某些逻辑]
 */
public interface JBasePresenter {

    /**
     * 默认需要初始加载的逻辑，拿数据
     */
    void subscribe();

    /**
     * for rxjava
     */
    void unsubscribe();

}