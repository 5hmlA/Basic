package com.zuyun.blueprint.vp.me;


import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.zuyun.blueprint.vp.basic.BaseTitleFrgmt;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [我]
 */
public class MeFragmt extends BaseTitleFrgmt {
    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        mMultiStateLayout.showStateSucceed();

    }

    @Override
    protected String setTitle(){
        return "我";
    }
}
