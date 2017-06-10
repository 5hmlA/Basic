package com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zuyun.blueprint.JApp;
import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemRecomWorm;
import com.blueprint.adapter.RecyclerHolder;
import com.blueprint.rx.RxBus;
import com.zuyun.blueprint.R;

import com.blueprint.LibApp;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author 江祖赟.
 * @date 2017/6/8
 * @des [一句话描述]
 */
public class ItemRecomWormBinder extends ItemViewBinder<ItemRecomWorm,RecyclerHolder> {
    @NonNull
    @Override
    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
        View rootView = inflater.inflate(R.layout.recv_item_recom_msgcard, null);
        return new RecyclerHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecyclerHolder holder, @NonNull ItemRecomWorm item){
        if(TextUtils.isEmpty(item.getAdvDesc())) {
            holder.setVisibility(R.id.tv_recv_recom_adv_desc, View.GONE);
            holder.setText(R.id.btn_recv_recom_adv, item.getAdvDesc());
        }else {
            holder.setText(R.id.tv_recv_recom_adv_desc, item.getAdvDesc());
            holder.setText(R.id.btn_recv_recom_adv, LibApp.findString(R.string.recom_welfare_enter));
        }

        holder.setText(R.id.tv_recv_recom_adv_name, item.getAdvName());
        holder.setImageUrl(R.id.im_recv_recom_adv_icon, item.getImageUrl());
        holder.setOnClickListener(R.id.btn_recv_recom_adv, new View.OnClickListener() {
            @Override
            public void onClick(View v){
                holder.setText(R.id.tv_recv_recom_adv_name, holder.getAdapterPosition()+"");
                Toast.makeText(JApp.getInstance(), "44:"+holder.getAdapterPosition(), Toast.LENGTH_LONG);
                RxBus.getInstance().post(holder.getAdapterPosition()+"");
                RxBus.getInstance().post(holder.getAdapterPosition());
            }
        });
    }
}
