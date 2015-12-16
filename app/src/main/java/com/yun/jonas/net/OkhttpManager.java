package com.yun.jonas.net;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jonas on 2015/11/22.
 */
public class OkhttpManager {

    public OkHttpClient client;
    private Gson gson;
    private static OkhttpManager sNetworkManager;


    private OkhttpManager(){
        client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);
        this.gson = new Gson();
        Cache mCache = new Cache(Environment.getDataDirectory(), 10*1024*1024);
        client.setCache(mCache);
    }

    public static OkhttpManager getInstance(){
        if(sNetworkManager == null) {
            synchronized(OkhttpManager.class) {
                if(sNetworkManager == null) {
                    sNetworkManager = new OkhttpManager();
                }
            }
        }
        return sNetworkManager;
    }

    public void request(Request r, final NetResult result){
        client.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e){
                result.onFailure(request);
            }

            @Override
            public void onResponse(Response response) throws IOException{
                result.onSucceed(response);
            }
        });
    }

    /**
     * 将返回的数据封装成 对象
     * @param r
     * @param result
     * @param aClass
     * @param <T>  返回的对象
     */
    public <T> void requestObject(Request r, final NetResult<Request,T> result, final Class<T> aClass){
        client.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e){
                result.onFailure(request);
            }

            @Override
            public void onResponse(Response response) throws IOException{
                T modle = gson.fromJson(response.body().charStream(), aClass);
                result.onSucceed(modle);
            }
        });
        Log.d("request", r.urlString());
    }
}
