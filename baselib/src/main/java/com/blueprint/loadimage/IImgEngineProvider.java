package com.blueprint.loadimage;

import android.content.Context;

/**
 * @author 江祖赟.
 * @date 2017/6/9
 * @des [一句话描述]
 */
public interface IImgEngineProvider {
    void loadNormal(Context ctx,ImgShowConfig config);
    void loadCache(Context ctx,ImgShowConfig config);
}
