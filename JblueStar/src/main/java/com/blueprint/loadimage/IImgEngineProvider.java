package com.blueprint.loadimage;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * @author 江祖赟.
 * @date 2017/6/9
 * @des [一句话描述]
 */
public interface IImgEngineProvider {
    void loadNormal(Context ctx, ImgShowConfig config);

    void loadNormal(ImageView iv, String url);

    void loadNormal(ImageView iv, String url, int width, int height);

    void loadNormal2(ImageView iv, String url, @DrawableRes int reserr,@DrawableRes int resloading);

    void loadCache(Context ctx, ImgShowConfig config);
    int setErrorHolder();
}
