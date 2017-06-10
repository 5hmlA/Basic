package com.blueprint.http;

import android.text.TextUtils;

import com.blueprint.LibApp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.blueprint.helper.CheckHelper.checkNotNull;
import static com.blueprint.helper.NetHelper.isConnected;

public class OkHttpProvider {

    private final static long DEFAULT_CONNECT_TIMEOUT = 5;
    private final static long DEFAULT_WRITE_TIMEOUT = 20;
    private final static long DEFAULT_READ_TIMEOUT = 10;
    private static final String OKHTTPCACHE = "OkHttpCache";

    public static OkHttpClient getDefaultOkHttpClient(){
        return getOkHttpClient(new CacheControlInterceptor());
    }

    public static OkHttpClient getOkHttpClient(){
        return getOkHttpClient(new FromNetWorkControlInterceptor());
    }

    private static OkHttpClient getOkHttpClient(Interceptor cacheControl){
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存
        File httpCacheDirectory = new File(LibApp.getContext().getCacheDir(), OKHTTPCACHE);
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 100*1024*1024));
        //设置拦截器
        httpClientBuilder.addInterceptor(new UserAgentInterceptor("Android Device"));
        httpClientBuilder.addInterceptor(cacheControl);
        httpClientBuilder.addNetworkInterceptor(cacheControl);
        return httpClientBuilder.build();
    }

    /**
     * 没有网络的情况下就从缓存中取
     * 有网络的情况则从网络获取
     */
    private static class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException{
            Request request = chain.request();
            if(!isConnected()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if(isConnected()) {
                int maxAge = 60;//在有网络连接的情况下，一分钟内不再请求网络
                String cacheControl = request.cacheControl().toString();
                if(TextUtils.isEmpty(cacheControl)) {
                    cacheControl = "public, max-age="+maxAge;
                }
                response = response.newBuilder().removeHeader("Pragma").header("Cache-Control", cacheControl).build();
            }else {
                int maxStale = 60*60*24*30;
                response = response.newBuilder().removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale="+maxStale).build();
            }
            return response;
        }
    }

    /**
     * 强制从网络获取数据
     */
    private static class FromNetWorkControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException{
            Request request = chain.request();
            request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();

            Response response = chain.proceed(request);

            return response;
        }
    }


    private static class UserAgentInterceptor implements Interceptor {
        private static final String USER_AGENT_HEADER_NAME = "User-Agent";
        private final String userAgentHeaderValue;

        UserAgentInterceptor(String userAgentHeaderValue){
            this.userAgentHeaderValue = checkNotNull(userAgentHeaderValue, "userAgentHeaderValue = null");
        }

        @Override
        public Response intercept(Chain chain) throws IOException{
            final Request originalRequest = chain.request();
            final Request requestWithUserAgent = originalRequest.newBuilder().removeHeader(USER_AGENT_HEADER_NAME)
                    .addHeader(USER_AGENT_HEADER_NAME, userAgentHeaderValue).build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
