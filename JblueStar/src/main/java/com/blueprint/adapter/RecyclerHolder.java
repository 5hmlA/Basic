package com.blueprint.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
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
    private final Context mContext;
    public int position;
    public int viewType;

    public <E> E getExtra(){
        return (E)extra;
    }

    public <E> void setExtra(E extra){
        this.extra = extra;
    }

    private Object extra;

    public RecyclerHolder(View itemView){
        super(itemView);
        mContext = itemView.getContext();
        mCacheViews = new SparseArray<>(10);
    }

    public Context getContext(){
        return mContext;
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

    public RecyclerHolder setText(int viewId, CharSequence text){
        TextView textView = getView(viewId);
        if(!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            if(!text.equals(textView.getText())) {
                textView.setText(text);
            }
        }else {
            textView.setVisibility(View.GONE);
        }
        return this;
    }

    public RecyclerHolder setText(int viewId, CharSequence text, int colorRes){
        TextView textView = getView(viewId);
        if(!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorRes));
            if(!text.equals(textView.getText())) {
                textView.setText(text);
            }
        }else {
            textView.setVisibility(View.GONE);
        }
        return this;
    }

    public RecyclerHolder setText2(int viewId, CharSequence text, @ColorInt int color){
        TextView textView = getView(viewId);
        if(!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(color);
            if(!text.equals(textView.getText())) {
                textView.setText(text);
            }
        }else {
            textView.setVisibility(View.GONE);
        }
        return this;
    }

    public RecyclerHolder setText(int viewId, int strRes){
        TextView textView = getView(viewId);
        if(textView != null) {
            String text = textView.getContext().getResources().getString(strRes);
            if(!text.equals(textView.getText())) {
                textView.setText(text);
            }
        }
        return this;
    }

    public RecyclerHolder setVisibility(int viewId, int visibility){
        View view = getView(viewId);
        if(view != null) {
            view.setVisibility(visibility);
        }
        return this;
    }

    public RecyclerHolder goneViews(int... viewId){
        for(int i : viewId) {
            View view = getView(i);
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public RecyclerHolder visibleViews(int... viewId){
        for(int i : viewId) {
            View view = getView(i);
            view.setVisibility(View.VISIBLE);
        }
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
        if(view != null) {
            view.setImageResource(drawableId);
        }
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageUrl(int viewId, String url){
        ImageView view = getView(viewId);
        if(view != null) {
            if(!TextUtils.isEmpty(url)) {
                view.setVisibility(View.VISIBLE);
                PicHelper.loadImage(url, view);
            }else {
                view.setVisibility(View.GONE);
            }
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
                PicHelper.loadImage(url, view, reWidth, reHeight);
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
            PicHelper.loadImage("file:///android_asset/"+picPath, view, reWidth, reHeight);
        }
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public RecyclerHolder setImageAsset(int viewId, String picPath){
        if(!TextUtils.isEmpty(picPath)) {
            ImageView view = getView(viewId);
            //            if(!isEqual(view.getTag(EQUALTAG), picPath)) {
            //                view.setTag(EQUALTAG, picPath);//不是同一个url才重新显示图片
            //            }
            PicHelper.loadImage("file:///android_asset/"+picPath, view);
        }
        return this;
    }

    public RecyclerHolder setOnLongClickListener(int viewId, View.OnLongClickListener l){
        View view = getView(viewId);
        if(view != null) {
            view.setOnLongClickListener(l);
        }
        return this;
    }

    public RecyclerHolder setOnClickListener(int viewId, View.OnClickListener l){
        View view = getView(viewId);
        if(view != null) {
            if(l != null) {
                view.setOnClickListener(l);
            }else {
                view.setClickable(false);
            }
        }
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

    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag = tag;
    }
}