package com.blueprint.helper;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blueprint.JSettingCenter;
import com.blueprint.loadimage.ImgEnginePrivoder;
import com.blueprint.loadimage.ImgShowConfig;
import com.squareup.picasso.Picasso;

import static com.blueprint.helper.NetHelper.isWifionnected;

public class PicHelper {

    public static void loadImage(ImgShowConfig imgShowConfig){
        Context context = imgShowConfig.getImageView().getContext();
        if(!JSettingCenter.getOnlyWifiLoadImage()) {
            loadNormal(context, imgShowConfig);
        }else {
            if(isWifionnected()) {
                loadNormal(context, imgShowConfig);
            }else {
                loadCache(context, imgShowConfig);
            }
        }

    }

    public static void loadNormal(Context ctx, ImgShowConfig img){
        ImgEnginePrivoder.getInstance().loadNormal(ctx, img);
    }

    public static void loadCache(Context ctx, ImgShowConfig img){
        ImgEnginePrivoder.getInstance().loadCache(ctx, img);
    }

    public static void loadImage(String url, ImageView view){
        if(!TextUtils.isEmpty(url)) {
            Picasso.with(view.getContext()).load(url).centerCrop().into(view);
        }
    }

    public static void loadImage(String url, ImageView view, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(url)) {
            Picasso.with(view.getContext()).load(url).resize(reWidth, reHeight).centerCrop().into(view);
        }
    }

    public static void loadImage(String url, ImageView view, int resPlace, int resError, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(url)) {
            Picasso.with(view.getContext()).load(url).placeholder(resPlace).error(resError).resize(reWidth, reHeight)
                    .centerCrop().into(view);
        }
    }

    public static void loadAssetImage(String assetPath, ImageView view){
        if(!TextUtils.isEmpty(assetPath)) {
            Picasso.with(view.getContext()).load("file:///android_asset/"+assetPath).centerCrop().into(view);
        }
    }

    public static void loadAssetImage(String assetPath, ImageView view, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(assetPath)) {
            Picasso.with(view.getContext()).load("file:///android_asset/"+assetPath).resize(reWidth, reHeight)
                    .centerCrop().into(view);
        }
    }
}
