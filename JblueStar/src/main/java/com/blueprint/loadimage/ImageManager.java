package com.blueprint.loadimage;

/**
 * @another 江祖赟
 * @date 2017/7/28 0028.
 */
public class ImageManager {
    private ImageManager mImageManager;
    private IImgEngineProvider mIImgEngineProvider;

    private ImageManager(){

    }

    private static class Inner {
        private static ImageManager sImageManager = new ImageManager();
    }

    public static ImageManager getSingleton(){
        return Inner.sImageManager;
    }

    public void init(IImgEngineProvider iImgEngineProvider){
        mIImgEngineProvider = iImgEngineProvider;
    }

    public IImgEngineProvider getIImgEngineProvider(){
        if(mIImgEngineProvider == null) {
            throw new RuntimeException("建议在application中配置图片加载库(ImageManager.getSingleton().init())");
        }
        return mIImgEngineProvider;
    }
}
