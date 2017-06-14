package com.zuyun.blueprint.data.netsource;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by 4399-1500 on 2017/6/14.
 */

public interface DownloadService {
    String BASE_URL = "http://www.gank.io/api/";
    @GET("http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk")
    @Streaming
    Single download();
}
