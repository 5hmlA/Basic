package com.blueprint.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.reflect.Field;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    private final Gson mGson;
    private OkHttpClient mOkHttpClient;


    private ServiceFactory(){
        mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
        mOkHttpClient = OkHttpProvider.getDefaultOkHttpClient();

    }

    private static class SingletonHolder {
        private static final ServiceFactory INSTANCE = new ServiceFactory();
    }

    public static ServiceFactory getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public static ServiceFactory getNoCacheInstance(){
        ServiceFactory factory = SingletonHolder.INSTANCE;
        factory.mOkHttpClient = OkHttpProvider.getNetOkHttpClient();
        return factory;
    }
    public static ServiceFactory getCacheInstance(){
        ServiceFactory factory = SingletonHolder.INSTANCE;
        factory.mOkHttpClient = OkHttpProvider.getCacheOkHttpClient();
        return factory;
    }

    public <S> S createService(Class<S> serviceClass, String baseUrl){
        return createService(serviceClass, baseUrl, new Interceptor[0]);
    }

    public <S> S createService(Class<S> serviceClass, String baseUrl, Interceptor... interceptors){
        if(interceptors != null && interceptors.length>0) {
            for(Interceptor interceptor : interceptors) {
                mOkHttpClient.interceptors().add(interceptor);
            }
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        return retrofit.create(serviceClass);
    }

    public <S> S createService(Class<S> serviceClass){
        String baseUrl = "";
        try {
            Field field1 = serviceClass.getField("BASE_URL");
            baseUrl = (String)field1.get(serviceClass);
        }catch(NoSuchFieldException e) {
            e.printStackTrace();
        }catch(IllegalAccessException e) {
            e.getMessage();
            e.printStackTrace();
        }
        return createService(serviceClass, baseUrl);
    }


}
