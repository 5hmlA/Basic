package com.zuyun.blueprint.data.bean;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [一句话描述]
 */
public class MeiZhi {

    /**
     * _id : 5936223c421aa92c73b647c7
     * createdAt : 2017-06-06T11:32:12.609Z
     * desc : 6-6
     * publishedAt : 2017-06-06T11:36:13.568Z
     * source : chrome
     * type : 福利
     * url : https://ws1.sinaimg.cn/large/610dc034ly1fgbbp94y9zj20u011idkf.jpg
     * used : true
     * who : dmj
     */

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;

    public String get_id(){
        return _id;
    }

    public void set_id(String _id){
        this._id = _id;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getPublishedAt(){
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt){
        this.publishedAt = publishedAt;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public boolean isUsed(){
        return used;
    }

    public void setUsed(boolean used){
        this.used = used;
    }

    public String getWho(){
        return who;
    }

    public void setWho(String who){
        this.who = who;
    }
}
