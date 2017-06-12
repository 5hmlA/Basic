package com.blueprint.loadimage;

import android.content.Context;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * Created by _SOLID
 * Date:2016/5/13
 * Time:10:27
 */
public class ImgEnginePrivoder implements IImgEngineProvider {

    private static class Inner {
        private static ImgEnginePrivoder engine = new ImgEnginePrivoder();
    }

    public static ImgEnginePrivoder getInstance(){
        return Inner.engine;
    }

    @Override
    public void loadNormal(Context ctx, ImgShowConfig config){
        Picasso.with(ctx).load(config.getUrl()).centerCrop().placeholder(config.getPlaceHolder())
                .error(config.getError()).into(config.getImageView());
    }

    @Override
    public void loadCache(Context ctx, ImgShowConfig config){
        Picasso.with(ctx).load(config.getUrl()).centerCrop().placeholder(config.getPlaceHolder())
                .error(config.getError()).networkPolicy(NetworkPolicy.OFFLINE).into(config.getImageView());
    }
}
