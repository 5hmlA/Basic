package com.blueprint.du.okh;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public class DownloadProgressInterceptor implements Interceptor {
    private ProgressListener listener;

    public DownloadProgressInterceptor(ProgressListener listener){
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException{
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse;
//        return originalResponse.newBuilder().body(new DownloadProgressResponseBody(originalResponse.body(), listener))
//                .build();
    }
}
