package com.yun.jonas.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yun.jonas.utills.UIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Jonas on 2015/11/22.
 */
public class VolleyManager {

    public Gson gson;
    private static VolleyManager sNetworkManager;
    public final RequestQueue mRequestQueue;


    private VolleyManager(){
        mRequestQueue = Volley.newRequestQueue(UIUtils.getContext());
        this.gson = new Gson();
    }

    public static VolleyManager getInstance(){
        if(sNetworkManager == null) {
            synchronized(VolleyManager.class) {
                if(sNetworkManager == null) {
                    sNetworkManager = new VolleyManager();
                }
            }
        }
        return sNetworkManager;
    }

    public void canll(Objects tag){
        mRequestQueue.cancelAll(tag);
    }

    public void requestString(String url, Object tag, final NetResult result){
        requestString(Request.Method.GET, url, null, tag, result);
    }

    public void postRequestString(String url, Object tag, final NetResult result){
        requestString(Request.Method.POST, url, null, tag, result);
    }

    private void requestString(int method, String url, final Map<String,String> param, Object tag, final NetResult result){
        UtfRequest request = new UtfRequest(method, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s){
                result.onSucceed(s);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError){
                result.onFailure(volleyError);
            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                return param;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }
// private void requestString(int method, String url, final Map<String,String> param, Object tag, final NetResult result){
//        StringRequest request = new StringRequest(method, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String s){
//                result.onSucceed(s);
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError){
//                result.onFailure(volleyError);
//            }
//        }) {
//            @Override
//            protected Map<String,String> getParams() throws AuthFailureError{
//                return param;
//            }
//        };
//        request.setTag(tag);
//        mRequestQueue.add(request);
//    }


    public void requestJsonObject(String url, Object tag, final NetResult result){
        requestJsonObject(Request.Method.GET, url, null, tag, result);
    }

    public void postRequestJsonObject(String url, Object tag, final NetResult result){
        requestJsonObject(Request.Method.POST, url, null, tag, result);
    }

    private void requestJsonObject(int method, String url, final Map<String,String> param, Object tag, final NetResult result){
        JsonObjectRequest request = new JsonObjectRequest(method, url, null, new com.android.volley.Response.Listener<JSONObject>() {
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
            protected Map<String,String> getParams() throws AuthFailureError{
                return param;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }

    public void requestJsonArray(String url, final Map<String,String> param, Object tag, final NetResult result){
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
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                return param;
            }
        };
        request.setTag(tag);
        mRequestQueue.add(request);
    }

}
