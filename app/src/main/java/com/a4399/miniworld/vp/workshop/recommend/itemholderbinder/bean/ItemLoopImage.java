package com.a4399.miniworld.vp.workshop.recommend.itemholderbinder.bean;

import java.util.List;

/**
 * @author 江祖赟.
 * @date 2017/6/8
 * @des [一句话描述]
 */
public class ItemLoopImage {
    private List<String> imageUrls;

    public ItemLoopImage(List<String> imageUrls){
        this.imageUrls = imageUrls;
    }

    public List<String> getImageUrls(){
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls){
        this.imageUrls = imageUrls;
    }
}
