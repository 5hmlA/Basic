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
public class NetworkManager {

    public OkHttpClient client;
    private Gson gson;
    private static NetworkManager sNetworkManager;


    private NetworkManager(){
        client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);
        this.gson = new Gson();
        Cache mCache = new Cache(Environment.getDataDirectory(), 10*1024*1024);
        client.setCache(mCache);
    }

    public static NetworkManager getInstance(){
        if(sNetworkManager == null) {
            synchronized(NetworkManager.class) {
                if(sNetworkManager == null) {
                    sNetworkManager = new NetworkManager();
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

    public <T> void request(Request r, final NetResult result, final Class<T> aClass){
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
