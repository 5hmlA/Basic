package com.zuyun.blueprint.vp.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueprint.widget.LazyFragment;
import com.zuyun.blueprint.R;
import com.orhanobut.logger.Logger;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class BaseFragment extends LazyFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View inflate = inflater.inflate(R.layout.main_fm_content, null);
        TextView mTempTv = (TextView)inflate.findViewById(R.id.temp_tv);
        mTempTv.setText("232");
        return inflate;
    }


    @Override
    public void firstUserVisibile(){
        Logger.d("firstUserVisibile---");
    }
}
