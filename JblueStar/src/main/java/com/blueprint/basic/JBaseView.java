package com.blueprint.basic;

import com.blueprint.error.ErrorMsg;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public interface JBaseView {

    void showLoading();

    void showError(ErrorMsg emsg);

}
