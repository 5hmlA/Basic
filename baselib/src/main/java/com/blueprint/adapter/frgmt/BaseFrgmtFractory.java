package com.blueprint.adapter.frgmt;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public abstract class BaseFrgmtFractory {
    protected SparseArray<Fragment> fmCache = new SparseArray<Fragment>();
    public abstract Fragment createFragment(int position);
}
