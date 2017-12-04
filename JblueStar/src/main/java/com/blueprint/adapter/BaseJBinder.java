//package com.blueprint.adapter;
//
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.blueprint.helper.interf.IRecvJData;
//import com.blueprint.helper.interf.OnItemClickListener;
//
//import me.drakeet.multitype.ItemViewBinder;
//
///**
// * @another 江祖赟
// * @date 2017/6/30.
// */
//public class BaseJBinder<D extends IRecvJData> extends ItemViewBinder<D,RecyclerHolder> {
//
//    OnItemClickListener<D> mItemClickListener;
//
//    public BaseJBinder(OnItemClickListener<D> itemClickListener){
//        mItemClickListener = itemClickListener;
//    }
//
//    @NonNull
//    @Override
//    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
////                return new RecyclerHolder(inflater.inflate(mLayoutIds, parent, false));
//        return null;
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull final RecyclerHolder holder, @NonNull final D item){
//        item.bindHolder(holder.getContext());
//        if(mItemClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//                    mItemClickListener.onItemClicked(item, holder.getAdapterPosition());
//                }
//            });
//        }
//    }
//}
