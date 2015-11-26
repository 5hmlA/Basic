package com.yun.jonas.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.yun.jonas.R;
import com.yun.jonas.application.BaseApplication;
import com.yun.jonas.base.BaseFragment;
import com.yun.jonas.base.BaseListAdapter;
import com.yun.jonas.holder.ViewHolderHelper;
import com.yun.jonas.model.Recommend;
import com.yun.jonas.model.bdmeizhi;
import com.yun.jonas.utills.OttConstants;
import com.yun.jonas.utills.UIUtils;
import com.yun.jonas.widget.LoopImageHolder;

import java.util.ArrayList;
import java.util.List;


public class Twofm extends BaseFragment {

    private LoopImageHolder mLoopImageHolder;
    private LinearLayout mLlrecommend;
    private List<Recommend> mRec_data;


    @Override
    protected <T> int onNetSucceed(T response){
        Gson gson = new Gson();
        bdmeizhi bdmeizhi = null;
        bdmeizhi = gson.fromJson((String)response, com.yun.jonas.model.bdmeizhi.class);
        mRec_data = new ArrayList<Recommend>();

        List<String> url_maps = new ArrayList<>();
        for(int i = 0; i<bdmeizhi.getData().size()-1; i++) {
            mRec_data.add(new Recommend("今日热点："+i, bdmeizhi));
            url_maps.add(bdmeizhi.getData().get(i).getImage_url());
        }
        mLoopImageHolder = new LoopImageHolder(getActivity());
        mLoopImageHolder.setData(url_maps);

        return checkNetDataResult(mRec_data);
    }

    @Override
    protected String setRequestURL(){
        return OttConstants.baidumeinv;
    }


    @Override
    protected View createAfterNetScceed(){
        fmName = "Two";
        View recomment = View.inflate(UIUtils.getContext(), R.layout.fm_two, null);
        mLlrecommend = (LinearLayout)recomment.findViewById(R.id.ll_recommend);
        ListView lv_recommend = (ListView)recomment.findViewById(R.id.lv_recommend);

        lv_recommend.setAdapter(new BaseListAdapter<Recommend>(mRec_data) {
            @Override
            protected ViewHolderHelper getAdapterHelper(int position, View convertView, ViewGroup parent){
                return ViewHolderHelper.get(UIUtils.getContext(), convertView, parent, R.layout.item_list_recommend, position);
            }

            @Override
            protected void convert(ViewHolderHelper helper, Recommend item){
                helper.setText(R.id.tv_list_title, item.title);
                helper.setClickListener(R.id.tv_list_rec_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                    }
                });


                helper.setAdapter(R.id.ingv_recommend, new BaseListAdapter<bdmeizhi.DataEntity>(item.meizhi.getData()) {
                    @Override
                    protected ViewHolderHelper getAdapterHelper(int position, View convertView, ViewGroup parent){
                        return ViewHolderHelper.get(UIUtils.getContext(), convertView, parent, R.layout.item_gridview_recommend, position);
                    }

                    @Override
                    protected void convert(ViewHolderHelper helper, final bdmeizhi.DataEntity item){
                        helper.setText(R.id.tv_item_grid_recommend, item.getAbs());
                        int image_width = item.getImage_width();
                        float v = image_width*1f/( BaseApplication.screenW*1f/3*1f );
                        int height = (int)(item.getImage_height()/v);
                        if(height!=0) {
                            helper.setImageFromUrl(R.id.im_item_grid_recommend, item.getImage_url(),BaseApplication.screenW/3,height);
                        }
                        helper.setClickListener(R.id.tv_item_grid_recommend, new View.OnClickListener() {

                            @Override
                            public void onClick(View v){
                            }
                        });
                    }

                });
            }
        });

        mLoopImageHolder.setOnItemClickListener(new LoopImageHolder.onItemClickListener() {
            @Override
            public <T> void onItemClicked(T data, int position){
            }
        });
        lv_recommend.addHeaderView(mLoopImageHolder.getLoopImageView());
        return recomment;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mLoopImageHolder != null) {
            mLoopImageHolder.toogleAsyncLoop(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(mLoopImageHolder != null) {
            mLoopImageHolder.toogleAsyncLoop(isVisibleToUser);
        }
    }
}
