package com.blueprint.du.okh;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public class DownLoadEntity {
    public int dataId;
    public String url;
    public long end;
    public long start;
    public long downed;
    public long total;
    public String saveName;
    public List<DownLoadEntity> multiList;

    public DownLoadEntity(String url, String saveName){
        this.url = url;
        this.saveName = saveName;
    }
}
