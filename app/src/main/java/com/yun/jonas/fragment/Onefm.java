package com.yun.jonas.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.yun.jonas.R;
import com.yun.jonas.base.BaseFragment;


public class Onefm extends BaseFragment {


    @Override
    protected View createAfterNetScceed(){
        return null;
    }

    @Override
    protected <T> int onNetSucceed(T response){
        return STATE_SUCCEED;
    }

    @Override
    protected String setRequestURL(){
        return "https://www.google.com/chrome/?&brand=CHWL&utm_campaign=en&utm_source=en-et-na-us-chrome-bubble&utm_medium=et";
    }


    @Override
    protected View setbeforeSucceedContent(LayoutInflater inflater){
        fmName = "mytvfm";
        return inflater.inflate(R.layout.fm_one, null);
    }

}
