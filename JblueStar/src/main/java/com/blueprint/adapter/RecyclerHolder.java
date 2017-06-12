package com.blueprint.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.blueprint.helper.PicHelper;

/**
 * @des [recycleview相关 holder基类]
 */
public class RecyclerHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mCacheViews;
    private String tag = RecyclerHolder.class.getSimpleName();
    public static final String TAG_LOADING = "loadingholder";

    public RecyclerHolder(View itemView){
        super(itemView);
        mCacheViews = new SparseArray<>(10);
    }

    public <V extends View> V getView(int viewId){
        View view = mCacheViews.get(viewId);
        if(view == null) {
            view = itemView.findViewById(viewId);
            mCacheViews.put(viewId, view);
        }
        return (V)view;
    }

    public <V extends View> V getView(View rootView, int viewId){
        //        View view = mCacheViews.get(viewId);
        //        if(view == null) {
        View view = rootView.findViewById(viewId);
        //            mCacheViews.put(viewId, view);
        //        }
        return (V)view;
    }

    public RecyclerHolder setText(int viewId, String text){
        if(!TextUtils.isEmpty(text)) {
            TextView textView = getView(viewId);
            textView.setText(text);
        }
        return this;
    }

    public RecyclerHolder setText(int viewId, int strRes){
        TextView textView = getView(viewId);
        textView.setText(textView.getContext().getResources().getString(strRes));
        return this;
    }

    public RecyclerHolder setVisibility(int viewId, int visibility){
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public int getVisibility(int viewId){
        View view = getView(viewId);
        return view.getVisibility();
    }

    public RecyclerHolder setNumStars(int viewId, int numStars){
        RatingBar ratingBar = getView(viewId);
        ratingBar.setNumStars(numStars);
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageResource(int viewId, int drawableId){
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageUrl(int viewId, String url){
        if(!TextUtils.isEmpty(url)) {
            if(url.startsWith("http")) {
                ImageView view = getView(viewId);
                PicHelper.loadImage(url, view);
            }else {
                setImageAsset(viewId, url);
            }
        }else {
            setImageResource(viewId, com.blueprint.R.mipmap.ic_launcher);
        }
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageUrl(int viewId, String url, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(url)) {
            if(url.startsWith("http")) {
                ImageView view = getView(viewId);
                //                Picasso.with(view.getContext()).load(url).resize(reWidth, reHeight).centerCrop().into(view);
            }else {
                setImageAsset(viewId, url, reWidth, reHeight);
            }
        }
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageBitmap(int viewId, Bitmap bm){
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageAsset(int viewId, String picPath, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(picPath)) {
            ImageView view = getView(viewId);
            //            Picasso.with(view.getContext()).load("file:///android_asset/img/" + picPath).resize(reWidth, reHeight).centerCrop().into(view);
        }
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageAsset(int viewId, String picPath){
        if(!TextUtils.isEmpty(picPath)) {
            ImageView view = getView(viewId);
            //            Picasso.with(view.getContext()).load("file:///android_asset/img/" + picPath).into(view);
        }
        return this;
    }

    public RecyclerHolder setOnClickListener(int viewId, View.OnClickListener l){
        View view = getView(viewId);
        view.setOnClickListener(l);
        return this;
    }

    public RecyclerHolder setOnClickListener(View.OnClickListener l){
        this.itemView.setOnClickListener(l);
        return this;
    }

    public RecyclerHolder setOnLongclickListener(View.OnLongClickListener l){
        this.itemView.setOnLongClickListener(l);
        return this;
    }

    //    /**
    //     * 为ImageView设置图片
    //     *
    //     * @param viewId
    //     * @param url
    //     * @return
    //     */
    //    public RecyclerHolder setImageByUrl(int viewId, String url) {
    //      return this;
    //    }


    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag = tag;
    }
}