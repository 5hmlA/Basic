package com.a4399.miniworld.vp.dynamic;


import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.a4399.miniworld.vp.basic.BaseTitleFrgmt;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [动态]
 */
public class DynamicFrgmt extends BaseTitleFrgmt {
    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){

    }

    @Override
    protected String setTitle(){
        return "动态";
    }
}