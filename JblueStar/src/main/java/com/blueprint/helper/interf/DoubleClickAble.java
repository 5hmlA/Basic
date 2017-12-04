package com.blueprint.helper.interf;

/**
 * @another 江祖赟
 * @date 2017/10/20 0020.
 */
public interface DoubleClickAble {
    void setOnDoubleClickListener(OnDoubleClickListener dl);

    interface OnDoubleClickListener {
        void onDoubleClicked(DoubleClickAble view);
    }
}
