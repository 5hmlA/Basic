package com.a4399.miniworld.vp.workshop.recommend.itemholderbinder.bean;

/**
 * @author 江祖赟.
 * @date 2017/6/8
 * @des [一句话描述]
 */
public class ItemRecomWorm {
    private String imageUrl;
    private String advName;
    private String advDesc;
    private int type = -1;


    public ItemRecomWorm(String imageUrl, String advName, String advDesc, int type){
        this.imageUrl = imageUrl;
        this.advName = advName;
        this.advDesc = advDesc;
        this.type = type;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getAdvName(){
        return advName;
    }

    public void setAdvName(String advName){
        this.advName = advName;
    }

    public String getAdvDesc(){
        return advDesc;
    }

    public void setAdvDesc(String advDesc){
        this.advDesc = advDesc;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }

    public static class ItemRecomWormBuilder {
        private String mImageUrl;
        private String mAdvName;
        private String mAdvDesc;
        private int mType;

        public ItemRecomWormBuilder setImageUrl(String imageUrl){
            mImageUrl = imageUrl;
            return this;
        }

        public ItemRecomWormBuilder setAdvName(String advName){
            mAdvName = advName;
            return this;
        }

        public ItemRecomWormBuilder setAdvDesc(String advDesc){
            mAdvDesc = advDesc;
            return this;
        }

        public ItemRecomWormBuilder setType(int type){
            mType = type;
            return this;
        }

        public ItemRecomWorm build(){
            return new ItemRecomWorm(mImageUrl, mAdvName, mAdvDesc, mType);
        }
    }
}
