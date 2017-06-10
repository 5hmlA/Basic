package com.zuyun.blueprint.data.netsource.urlapi;

import com.blueprint.http.HttpResult;
import com.zuyun.blueprint.data.bean.MeiZhi;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author 江祖赟.
 * @date 2017/6/6
 * @des [一句话描述]
 */
public interface MeiZhiService {
    String BASE_URL = "http://www.gank.io/api/";

    @GET("data/福利/20/{pageindex}")
    Single<HttpResult<List<MeiZhi>>> getMeizhi(@Path("pageindex") int page);

    @GET("data/福利/20/{pageindex}")
    Flowable<HttpResult<List<MeiZhi>>> getMeizhi2(@Path("pageindex") int page);
    /***
     * 根据类别查询干货
     * @param pageIndex
     * @return
     */
    @GET("data/福利/20/{pageIndex}")
    Single<HttpResult<List<MeiZhi>>> getGanHuo(@Path("pageIndex") int pageIndex);


}
