package com.blueprint.helper.interf;

import android.view.View;

/**
 * @another 江祖赟
 * @date 2017/6/21.
 */
public interface OnViewClickListener<T> {
    void onItemClicked(View view, T itemData);
}
