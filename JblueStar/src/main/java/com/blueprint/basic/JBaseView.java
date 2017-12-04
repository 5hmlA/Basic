package com.blueprint.basic;

import com.blueprint.Consistent;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public interface JBaseView<SD> {

    void showLoading();

    //todo 改成code+msg
    void showError(@Consistent.ErrorCode int eCode);

    void showError(Consistent.ErrorData error);

    void showSucceed(SD data);

}
