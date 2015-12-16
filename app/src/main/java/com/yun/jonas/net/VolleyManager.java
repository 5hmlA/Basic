package com.yun.jonas.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yun.jonas.utills.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jonas on 2015/11/22.
 */
public class VolleyManager {

    public Gson gson;
    private static VolleyManager sNetworkManager;
    public final RequestQueue mRequestQueue;
    private String cookie;


    private VolleyManager(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
        this.gson = new Gson();
    }

    private static VolleyManager create(Context context){
        if(sNetworkManager == null) {
            synchronized(VolleyManager.class) {
                if(sNetworkManager == null) {
                    sNetworkManager = new VolleyManager(context);
                }
            }
        }
        return sNetworkManager;
    }

    public void cancel(Objects tag){
        mRequestQueue.cancelAll(tag);
    }

    /**
     * GET请求 string数据
     *
     * @param url url记得带上？
     * @param urlparams
     *              将会拼在url后面
     * @param tag
     * @param result
     */
    public void requestString(String url, Map<String,String> urlparams, Object tag, final NetResult result){
        for(String key : urlparams.keySet()) {
            url += key+"="+urlparams.get(key).toString()+"&";
        }
        url.substring(0, url.length());
        requestString(Request.Method.GET, url, null, tag, result);
    }

    /**
     * POST请求string数据
     *
     * @param url
     * @param params
     * @param tag
     * @param result
     */
    public void postRequestString(String url, Map<String,String> params, Object tag, final NetResult result){
        requestString(Request.Method.POST, url, params, tag, result);
    }

    private void requestString(int method, String url, final Map<String,String> params, Object tag, final NetResult result){
        StringRequest request = new StringRequest(method, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s){
                result.onSucceed(s);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError){
                StackTraceElement[] stackTrace = volleyError.getStackTrace();
                for(int i = stackTrace.length-1; i>=0; i--) {
                    StackTraceElement stackTraceElement = stackTrace[i];
                    LogUtils.e(stackTraceElement.toString());
                }
                LogUtils.e(volleyError.getStackTrace()+"");
                result.onFailure(volleyError);
            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> mHeaders = new HashMap<>();
                mHeaders.put("Cookie", cookie);
                LogUtils.d("getCookie："+cookie);
                return mHeaders;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }


    /**
     * GET请求 jsonobject类型数据
     *
     * @param url
     *         接口后面带上？号
     * @param map
     *          GET后的请求参数
     * @param tag
     * @param result
     */
    public void requestJsonObject(String url, Map<String,String> map, Object tag, final NetResult result){
        for(String key : map.keySet()) {
            url += key+"="+map.get(key).toString()+"&";
        }
        url.substring(0, url.length());
        requestJsonObject(Request.Method.GET, url, tag, result);
    }

    /**
     * @param method
     * @param url
     * @param tag
     * @param result
     */
    private void requestJsonObject(int method, String url, Object tag, final NetResult result){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject){
                result.onSucceed(jsonObject);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError){
                result.onFailure(volleyError);
            }

        }) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> mHeaders = new HashMap<>();
                mHeaders.put("Cookie", cookie);
                LogUtils.d("getCookie："+cookie);
                return mHeaders;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }


    /**
     * POST请求 带有cookies   JSONID=.........
     *
     * @param url
     * @param tag
     * @param netResult
     */
    public void postParamJsonObject(String url, Map map, Object tag, final NetResult netResult){
        //        SparseArray优化的hashmap高效 但是键 只能是 int数据
        JSONObject json = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject){

                netResult.onSucceed(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError){

                netResult.onFailure(volleyError);
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response){
                Map<String,String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> mHeaders = new HashMap<>();
                mHeaders.put("Cookie", cookie);
                LogUtils.d("getCookie："+cookie);
                return mHeaders;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }

    /**
     * 只支持GET请求
     *
     * @param url
     * @param tag
     * @param result
     */
    public void requestJsonArray(String url, Object tag, final NetResult result){
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray){
                result.onSucceed(jsonArray);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError){
                result.onFailure(volleyError);
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> mHeaders = new HashMap<>();
                mHeaders.put("Cookie", cookie);
                LogUtils.d("getCookie："+cookie);
                return mHeaders;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }

    /**
     * 设置请求头中的cookie
     *
     * @param cookie
     */
    public void setCoolie(String cookie){
        this.cookie = cookie;
    }

}
