package com.a4399.miniworld.vp.basic;

import com.a4399.miniworld.Constants;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public interface BaseView {

    void showLoading();

    /**
     * 主要是 隐藏loading界面
     */
    void showSucceed();

    void showError(Constants.ErrorCode code);

}
