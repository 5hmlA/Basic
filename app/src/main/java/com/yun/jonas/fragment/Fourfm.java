package com.yun.jonas.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.yun.jonas.R;
import com.yun.jonas.application.BaseApplication;
import com.yun.jonas.base.BaseFragment;
import com.yun.jonas.base.BaseListAdapter;
import com.yun.jonas.holder.ViewHolderHelper;
import com.yun.jonas.model.bdmeizhi;
import com.yun.jonas.net.VolleyManager;
import com.yun.jonas.utills.OttConstants;
import com.yun.jonas.utills.UIUtils;

import java.util.List;

public class Fourfm extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private bdmeizhi mBdmeizhi;
    private final int CHINNEL_MOVIE = 0;
    private final int CHINNEL_SPOT = 2;
    private final int CHINNEL_TV = 3;
    private int chinnel = 4;
    private BaseListAdapter mAdapter;

    @Override
    protected void addHeadContent(LayoutInflater inflater, RelativeLayout rl_head_content){
        View inflate = inflater.inflate(R.layout.custom_headview, rl_head_content);
        CheckBox tvdemandfilter = (CheckBox)inflate.findViewById(R.id.tv_demand_filter);
        RadioGroup rgdemand = (RadioGroup)inflate.findViewById(R.id.rg_demand);
        //        RadioButton tvdemandsport = (RadioButton)inflate.findViewById(R.id.tv_demand_sport);
        //        RadioButton tvdemandtv = (RadioButton)inflate.findViewById(R.id.tv_demand_tv);
        //        RadioButton tvdemandmovie = (RadioButton)inflate.findViewById(R.id.tv_demand_movie);
        tvdemandfilter.setOnClickListener(this);
        rgdemand.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View v){
        super.onClick(v);
        if(v.getId() == R.id.tv_demand_filter) {
        }

    }

    @Override
    protected View createAfterNetScceed(){
        Log.e("顺序", "createAfterNetScceed"+mBdmeizhi.getData().size());
        fmName = "For";
        View view = View.inflate(UIUtils.getContext(), R.layout.fm_four, null);
        GridView gridview = (GridView)view.findViewById(R.id.gv_demdf);
        List<bdmeizhi.DataEntity> data = mBdmeizhi.getData();
        data.remove(data.size()-1);
        mAdapter = new BaseListAdapter<bdmeizhi.DataEntity>(data) {
            @Override
            protected ViewHolderHelper getAdapterHelper(int position, View convertView, ViewGroup parent){
                return ViewHolderHelper.get(Fourfm.this.getActivity(), convertView, parent, R.layout.item_grid_reco, position);
            }

            @Override
            protected void convert(ViewHolderHelper helper, bdmeizhi.DataEntity item){
                int width = BaseApplication.screenW/3-5;
                float scale = item.getImage_width()*1f/width;
                int height = (int)(item.getImage_height()*scale);

                helper.setImageFromUrl(R.id.iv_item_reco_grid, item.getImage_url());
                helper.setText(R.id.tv_item_reco_grid, item.getAbs());
            }
        };
        gridview.setAdapter(mAdapter);
        return view;
    }


    @Override
    protected String setRequestURL(){
        String url = null;
        if(chinnel == CHINNEL_MOVIE) {
            url = "http://image.baidu.com/channel/listjson?rn=6&tag1=美女&tag2=可爱&ie=utf8&pn=2";
        }else if(chinnel == CHINNEL_TV) {
            url = "http://image.baidu.com/channel/listjson?rn=6&tag1=美女&tag2=可爱&ie=utf8&pn=3";

        }else if(chinnel == CHINNEL_SPOT) {

            url = OttConstants.baidumeinv;
        }else {
            url = "http://image.baidu.com/channel/listjson?rn=6&tag1=美女&tag2=可爱&ie=utf8&pn=4";
        }
        return url;
    }

    @Override
    protected <T> int onNetSucceed(T response){
        Log.e("顺序", "onNetSucceed");
        Gson gson = VolleyManager.getInstance().gson;
        mBdmeizhi = null;
        mBdmeizhi = gson.fromJson((String)response, com.yun.jonas.model.bdmeizhi.class);
        return STATE_SUCCEED;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId){
        switch(checkedId) {
            case R.id.tv_demand_sport:
                chinnel = CHINNEL_SPOT;
                break;
            case R.id.tv_demand_tv:
                chinnel = CHINNEL_TV;
                break;
            case R.id.tv_demand_movie:
                chinnel = CHINNEL_MOVIE;
                break;
        }
        getNetData();
    }

    private void getNetData(){

        showPagerView(mState = STATE_LOADING);
        Context context = getActivity();
        if(context == null) {
            context = UIUtils.getContext();
        }
//        final RequestQueue requestQueue = Volley.newRequestQueue(context);
//
//        StringRequest request = new StringRequest(OttConstants.baidumeinv2+"3", new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String s){
//                System.out.println("成功"+s);
//                Gson gson = new Gson();
//                mBdmeizhi = null;
//                mBdmeizhi = gson.fromJson(s, com.yun.ott.model.bdmeizhi.class);
//                System.out.println(",,,,,,,,,,");
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError){
//
//                System.out.println("失败");
//            }
//        });
//        requestQueue.add(request);
        VolleyManager.getInstance().requestString(setRequestURL(), this, new StateResult(){
            @Override
            public <T> void onSucceed(T modle){
                int succeed = onNetSucceed(modle);
                mBdmeizhi.getData().remove(mBdmeizhi.getData().size()-1);
                mAdapter.setData(mBdmeizhi.getData());
                showSafePagerView(succeed);
            }
        });
    }

}
