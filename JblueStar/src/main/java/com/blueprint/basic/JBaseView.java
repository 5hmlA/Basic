package com.blueprint.basic;

import com.blueprint.Consistent;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public interface JBaseView<T> {

    void showLoading();

    void showError(Consistent.ErrorCode errorCode);

    void showSucceed(T data);

}
