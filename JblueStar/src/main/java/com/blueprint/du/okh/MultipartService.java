package com.blueprint.du.okh;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public interface MultipartService {



//    Ui/Image/uploadUnlimited.html
    @GET
    @Streaming
    Single<ResponseBody> download(@Url String url);
//非断点    http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk

    @Streaming
    @GET
    Single<ResponseBody> downloadRange(@Url String fileUrl, @Header("Range") String range);
//断电测试    https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk

    @Multipart
    @POST
    Single upload(@Url String url, @PartMap Map<String,RequestBody> files);

    @Multipart
    @POST
    Single upload(@Url String url, @Part MultipartBody.Part files);
}
