package com.zuyun.blueprint.vp.me;


import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.zuyun.blueprint.R;
import com.zuyun.blueprint.vp.basic.JBaseTitleFrgmt;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [我]
 */
public class MeFragmt extends JBaseTitleFrgmt {
    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){

        inflater.inflate(R.layout.main_me,container);
        mMultiStateLayout.showStateSucceed();

    }

    @Override
    protected String setTitle(){
        return null;
    }
}
