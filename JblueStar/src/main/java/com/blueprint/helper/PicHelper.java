package com.blueprint.helper;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blueprint.JSettingCenter;
import com.blueprint.loadimage.IImgEngineProvider;
import com.blueprint.loadimage.ImageManager;
import com.blueprint.loadimage.ImgShowConfig;

import static com.blueprint.helper.CheckHelper.EQUALTAG;
import static com.blueprint.helper.CheckHelper.isEqual;
import static com.blueprint.helper.NetHelper.isWifionnected;

public class PicHelper {

    public static void init(IImgEngineProvider iImgEngineProvider){
        ImageManager.getSingleton().init(iImgEngineProvider);
    }

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
        ImageManager.getSingleton().getIImgEngineProvider().loadNormal(ctx, img);
    }

    public static void loadCache(Context ctx, ImgShowConfig img){
        ImageManager.getSingleton().getIImgEngineProvider().loadCache(ctx, img);
    }

    public static void loadImage(String url, ImageView view){
        if(!isEqual(view.getTag(EQUALTAG), url)) {
            view.setTag(EQUALTAG, url);//不是同一个url才重新显示图片
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal(view, url);
        }else if(TextUtils.isEmpty(url)){
            view.setTag(EQUALTAG, null);
        }
    }

    public static void loadImage(String url, ImageView view, int reWidth, int reHeight){
        if(!isEqual(view.getTag(EQUALTAG), url)) {
            view.setTag(EQUALTAG, url);//不是同一个url才重新显示图片
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal(view, url, reWidth, reHeight);
        }else if(TextUtils.isEmpty(url)){
            view.setTag(EQUALTAG, null);
        }
    }

    public static void loadImage(String url, ImageView view, int resPlace, int resError, int reWidth, int reHeight){
        if(!isEqual(view.getTag(EQUALTAG), url)) {
            view.setTag(EQUALTAG, url);//不是同一个url才重新显示图片
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal(view, url, reWidth, reHeight);
        }else if(TextUtils.isEmpty(url)){
            view.setTag(EQUALTAG, null);
        }
    }

    public static void loadImage2(String url, ImageView view, int resError, int resLoading){
        if(!isEqual(view.getTag(EQUALTAG), url)) {
            view.setTag(EQUALTAG, url);//不是同一个url才重新显示图片
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal2(view, url, resError, resLoading);
        }else if(TextUtils.isEmpty(url)){
            view.setTag(EQUALTAG, null);
        }
    }

    public static void loadAssetImage(String assetPath, ImageView view){
        if(!TextUtils.isEmpty(assetPath)) {
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal(view, assetPath);
        }
    }

    public static void loadAssetImage(String assetPath, ImageView view, int reWidth, int reHeight){
        if(!TextUtils.isEmpty(assetPath)) {
            ImageManager.getSingleton().getIImgEngineProvider().loadNormal(view, assetPath, reWidth, reHeight);
        }

        //        Target target = new Target() {
        //            @Override
        //            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        //                imageView.setImageBitmap(bitmap);
        //                Drawable image = imageView.getDrawable();
        //            }
        //
        //            @Override
        //            public void onBitmapFailed(Drawable errorDrawable) {}
        //
        //            @Override
        //            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        //        };
        //
        //        Picasso.with(this).load("url").into(target);
    }
}
