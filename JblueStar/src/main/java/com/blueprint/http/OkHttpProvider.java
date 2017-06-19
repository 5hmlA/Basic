package com.blueprint.http;

import android.text.TextUtils;

import com.blueprint.LibApp;
import com.blueprint.JSettingCenter;
import com.blueprint.helper.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
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

    public static OkHttpClient getNetOkHttpClient(){
        return getOkHttpClient(new FromNetWorkControlInterceptor());
    }

    public static OkHttpClient getCacheOkHttpClient(){
        return getOkHttpClient(new FromCacheInterceptor());
    }

    private static OkHttpClient getOkHttpClient(Interceptor cacheControl){
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存
        File httpCacheDirectory = new File(LibApp.getContext().getCacheDir(), OKHTTPCACHE);
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 100*1024*30));
        //设置拦截器
        httpClientBuilder.addInterceptor(new UserAgentInterceptor("Android Device"));
        if(LibApp.isInDebug()) {
            httpClientBuilder.addInterceptor(new LoggingInterceptor());
        }

        //        注意：addInterceptor和addNetworkInterceptor 需要同时设置。
        // 如果 只是想实现在线缓存，那么可以只添加网络拦截器，如果只想实现离线缓存，可以使用只添加应用拦截器。
        httpClientBuilder.addInterceptor(cacheControl);
        httpClientBuilder.addNetworkInterceptor(cacheControl);

        httpClientBuilder.addNetworkInterceptor(new NetDataSaveInterceptor());
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
            if(!isConnected()) {//没有网络时设置强制读取缓存
                LogHelper.Log_d("没有连接，从本地获取缓存！");
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if(isConnected()) {
                int maxAge = 60;//在有网络连接的情况下，一分钟内不再请求网络
                //接口处可自定义 @Headers("Cache-Control: public, max-age=5")
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
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            return chain.proceed(request);
        }
    }

    /**
     * 强制从网络获取数据
     */
    private static class FromCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException{
            Request request = chain.request();
            request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            return chain.proceed(request);
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

    private static class NetDataSaveInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException{
            final Request originalRequest = chain.request();
            //对指定URL的请求保存网络数据  保存数据
            String url = originalRequest.url().toString();
            if(JSettingCenter.isNeedSaveData(url)) {
                //save to file
            }
            return chain.proceed(originalRequest);
        }
    }

    private static class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException{

            Request request = chain.request();
            LogHelper.Log_d(String
                    .format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            LogHelper.Log_d(String
                    .format(Locale.getDefault(), "Received response for %s in %.1fms%n%s", response.request().url(),
                            ( t2-t1 )/1e6d, response.headers()));

            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            LogHelper.Log_json(content);
            return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();
        }
    }
}
