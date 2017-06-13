package com.zuyun.blueprint.data.bean;

import java.util.List;

/**
 * Created by 4399-1500 on 2017/6/13.
 */

public class MeiZhis {
    public List<GanHuoData.Meizhi> mMeizhiList;

    public List<GanHuoData.Meizhi> getMeizhiList(){
        return mMeizhiList;
    }

    public void setMeizhiList(List<GanHuoData.Meizhi> meizhiList){
        mMeizhiList = meizhiList;
    }
}
